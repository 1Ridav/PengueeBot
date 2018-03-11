package bot.penguee;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clEnqueueWriteBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clSetKernelArg;

import java.awt.Rectangle;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

public class ScreenGPU extends Screen {

	private int resultArraySize = 100;

	private int resultFromGPUArray[] = new int[resultArraySize];
	// long global_work_size[] = new long[] { 1920, 1080 };
	// long local_work_size[] = new long[] { 20, 20 };
	// int kernel_instruction_buffers[] = new int[] { 1920, 1080, 24, 22, -2 };

	private long global_work_size[];
	// private long local_work_size[];
	private int kernel_instruction_buffers[];
	private cl_mem memObjects[] = new cl_mem[4];
	private cl_context context;
	private cl_command_queue commandQueue;
	private cl_program program;
	private cl_kernel kernel;
	private Pointer bigMatrixPointer;
	private Pointer resultFromGPUArrayPointer;
	private Pointer kernelInstrArrayPointer;
	private int[] bigMatrixArray;

	HashMap<String, cl_mem> fragMemObjects = new HashMap<String, cl_mem>();

	ScreenGPU() {
		super();

		String programSource = null;
		try (InputStream in = getClass().getResourceAsStream("/kernel/main.cl")) {
			Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name());
			programSource = scanner.useDelimiter("\\A").next();
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// The platform, device type and device number
		// that will be used
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;
		final int deviceIndex = 0;

		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Obtain the number of platforms
		int numPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		int numPlatforms = numPlatformsArray[0];

		// Obtain a platform ID
		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
		clGetPlatformIDs(platforms.length, platforms, null);
		cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		int numDevicesArray[] = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		int numDevices = numDevicesArray[0];

		// Obtain a device ID
		cl_device_id devices[] = new cl_device_id[numDevices];
		clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
		cl_device_id device = devices[deviceIndex];

		// Create a context for the selected device
		context = clCreateContext(contextProperties, 1, new cl_device_id[] { device }, null, null, null);

		// Create a command-recentScripts for the selected device
		commandQueue = clCreateCommandQueue(context, device, 0, null);

		// Create the program from the source code
		program = clCreateProgramWithSource(context, 1, new String[] { programSource }, null, null);

		// Build the program
		clBuildProgram(program, 0, null, null, null, null);

		// Create the kernel
		kernel = clCreateKernel(program, "findAll", null);

		global_work_size = new long[] { screenRect.width, screenRect.height };
		kernel_instruction_buffers = new int[] { screenRect.width, screenRect.height, 0, 0, -2 };

		createBuffers();
		loadFragments();
	}

	private int[] flat(int[][] array) {
		return Stream.of(array).flatMapToInt(Arrays::stream).toArray();
	}
	@Override
	public void grab() throws Exception {
		super.grab();
		loadScreenshotToGPU();
	}

	private void loadScreenshotToGPU() {
		int[][] matrix = screenFrag.getRgbData();
		int width = matrix[0].length;
		for (int i = 0; i < matrix.length; i++)
			System.arraycopy(matrix[i], 0, bigMatrixArray, width * i, width);

		clEnqueueWriteBuffer(commandQueue, memObjects[0], CL_TRUE, 0, bigMatrixArray.length * Sizeof.cl_int,
				bigMatrixPointer, 0, null, null);
	}
	@Override
	void grab_rect(int x, int y, int w, int h) throws Exception {
		super.grab_rect(new Rectangle(x, y, w, h));
		loadScreenshotToGPU();
	}
	@Override
	void grab_rect(MatrixPosition p1, MatrixPosition p2) throws Exception {
		super.grab_rect(new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y));
		loadScreenshotToGPU();
	}

	@Override
	void grab_rect(Rectangle rect) throws Exception {
		super.grab_rect(rect);
		loadScreenshotToGPU();
	}

	void loadFragments() {
		for (String k : Data.fragments.keySet()) {
			Frag smallFrag = Data.fragments.get(k);// get fragment
			int[] smallMatrix = flat(smallFrag.getRgbData());// copy rgbData to
																// 1d
			// buffer
			Pointer smallPointer = Pointer.to(smallMatrix); // create a pointer
															// to that 1d buffer
			// load data to GPU
			cl_mem memObj = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
					Sizeof.cl_int * smallMatrix.length, smallPointer, null);
			// get pointer to data on GPU
			fragMemObjects.put(k, memObj);
		}
	}

	public void createBuffers() {
		bigMatrixArray = new int[screenRect.width * screenRect.height];
		// load data
		bigMatrixPointer = Pointer.to(bigMatrixArray); // create a pointer to
														// that 1d buffer
		memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_int * bigMatrixArray.length, bigMatrixPointer, null);
		resultFromGPUArrayPointer = Pointer.to(resultFromGPUArray);
		kernelInstrArrayPointer = Pointer.to(kernel_instruction_buffers);

		memObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_int * resultArraySize, resultFromGPUArrayPointer, null);
		memObjects[3] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_int * kernel_instruction_buffers.length, kernelInstrArrayPointer, null);

	}

	public MatrixPosition[] findAll(String key) {

		Frag f = Data.fragments.get(key);
		int width = f.getRgbData()[0].length;
		int height = f.getRgbData().length;
		kernel_instruction_buffers[2] = width;
		kernel_instruction_buffers[3] = height;
		kernel_instruction_buffers[4] = -2;

		memObjects[1] = fragMemObjects.get(key);
		Arrays.fill(resultFromGPUArray, -1);
		clEnqueueWriteBuffer(commandQueue, memObjects[2], CL_TRUE, 0, resultArraySize * Sizeof.cl_int,
				resultFromGPUArrayPointer, 0, null, null);
		clEnqueueWriteBuffer(commandQueue, memObjects[3], CL_TRUE, 0, kernel_instruction_buffers.length * Sizeof.cl_int,
				kernelInstrArrayPointer, 0, null, null);

		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		// Execute the kernel
		clEnqueueNDRangeKernel(commandQueue, kernel, 2, null, global_work_size, null, 0, null, null);

		// Read the output data
		clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0, resultArraySize * Sizeof.cl_int,
				resultFromGPUArrayPointer, 0, null, null);
		// System.out.println(Arrays.toString(resultFromGPUArray));
		ArrayList<MatrixPosition> al = null;
		// MatrixPosition result = new MatrixPosition();
		for (int i = 0; i < resultFromGPUArray.length && resultFromGPUArray[i] != -1; i += 2) {
			if (al == null)
				al = new ArrayList<MatrixPosition>();
			al.add(new MatrixPosition(resultFromGPUArray[i], resultFromGPUArray[i + 1]));
		}

		if (searchInRegion && al != null) {

			for (int i = 0; i < al.size(); i++) {
				MatrixPosition frag = al.get(i);
				if (searchRectPos1.x <= frag.x && searchRectPos2.x >= frag.x && searchRectPos1.y <= frag.y
						&& searchRectPos2.y >= frag.y) {

				} else {
					al.remove(i);
				}
			}
		}

		MatrixPosition mpList[] = null;

		if (al != null)
			mpList = (MatrixPosition[]) al.toArray(new MatrixPosition[0]);
		return mpList;
	}

	public MatrixPosition find(String key) {
		MatrixPosition[] mp = findAll(key);
		MatrixPosition result = null;
		if (mp != null)
			result = mp[0];
		return result;
	}

	public MatrixPosition find(String name, String customName) {
		MatrixPosition mp = find(name);
		if (mp != null)
			return mp.setName(customName);

		return null;
	}

	public MatrixPosition[] find_all(String name, String customName) throws FragmentNotLoadedException {
		MatrixPosition mp[] = find_all(name);
		if (mp != null)
			for (int i = 0; i < mp.length; i++)
				mp[i].setName(customName);
		return mp;
	}
	/*
	 * public MatrixPosition[] go() { /* // Execute the kernel
	 * clEnqueueNDRangeKernel(commandQueue, kernel, 2, null, global_work_size,
	 * local_work_size, 0, null, null);
	 * 
	 * // Read the output data clEnqueueReadBuffer(commandQueue, memObjects[2],
	 * CL_TRUE, 0, resultArraySize Sizeof.cl_int, resultFromGPUArrayPointer, 0,
	 * null, null); //System.out.println(Arrays.toString(resultFromGPUArray));
	 * ArrayList al = null; // MatrixPosition result = new MatrixPosition(); for(int
	 * i = 0; i < resultFromGPUArray.length && resultFromGPUArray[i]!=-1; i+=2){
	 * if(al == null) al = new ArrayList(); al.add(new
	 * MatrixPosition(resultFromGPUArray[i], resultFromGPUArray[i+1])); }
	 * MatrixPosition mpList[] = null; if(al != null) mpList = (MatrixPosition[])
	 * al.toArray(new MatrixPosition[0]);
	 * 
	 * // Release kernel, program, and memory objects
	 * 
	 * clReleaseMemObject(memObjects[0]); clReleaseMemObject(memObjects[1]);
	 * clReleaseMemObject(memObjects[2]); clReleaseMemObject(memObjects[3]);
	 * clReleaseKernel(kernel); clReleaseProgram(program);
	 * clReleaseCommandQueue(commandQueue); clReleaseContext(context);
	 */
	// Verify the result
	/*
	 * boolean passed = true; final float epsilon = 1e-7f; for (int i=0;
	 * i<resultArraySize; i++) { float x = resultFromGPUArray[i]; float y =
	 * srcArrayA[i] * srcArrayB[i]; boolean epsilonEqual = Math.abs(x - y) <=
	 * epsilon * Math.abs(x); if (!epsilonEqual) { passed = false; break; } }
	 */
	// System.out.println("Test "+(passed?"PASSED":"FAILED"));
	// long t2 = System.nanoTime();
	// System.out.println("Time " + ((t2-t)));
	// System.out.println("Result: "+Arrays.toString(resultFromGPUArray));
	// return mpList;
	// }
}
