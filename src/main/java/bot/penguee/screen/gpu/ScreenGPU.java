package bot.penguee.screen.gpu;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_SUCCESS;
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
import static org.jocl.CL.clEnqueueWriteBufferRect;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clSetKernelArg;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

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

import bot.penguee.Data;
import bot.penguee.Position;
import bot.penguee.Region;
import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.exception.ScreenNotGrabbedException;
import bot.penguee.fragments.Frag;
import bot.penguee.screen.ScreenEngineInterface;

public class ScreenGPU implements ScreenEngineInterface {
	private final Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	Robot robot;
	BufferedImage screenImage;
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

	private boolean searchInRegion = false;
	private Region searchRect = null;

	HashMap<String, cl_mem> fragMemObjects = new HashMap<String, cl_mem>();

	public ScreenGPU() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

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

	private int[] flatten(int[][] array) {
		// FOR JAVA 1.8
		// return Stream.of(array).flatMapToInt(Arrays::stream).toArray();

		// FOR JAVA 1.7 ALLOW TO RUN ON WINDOWS XP
		int[] buff = new int[array.length * array[0].length];
		int x_len = array[0].length;
		int row_cache;
		int[] row_cache2;
		for (int y = 0; y < array.length; y++) {
			row_cache = y * x_len;
			row_cache2 = array[y];
			for (int x = 0; x < x_len; x++) {
				buff[row_cache + x] = row_cache2[x];
			}
		}
		return buff;
	}

	@Override
	public void grab() throws Exception {
		screenImage = robot.createScreenCapture(screenRect);
		final int pixels[] = ((DataBufferInt) screenImage.getData().getDataBuffer()).getData();
		clEnqueueWriteBuffer(commandQueue, memObjects[0], CL_TRUE, 0, pixels.length * Sizeof.cl_int, Pointer.to(pixels),
				0, null, null);
	}

	@Override
	public void grab(Rectangle rect) throws Exception {

		// ADD UPDATE CHECK, IT MAY FAIL SOMETIMES
		if (screenImage == null) {
			grab();
			return;
		}
		screenImage = robot.createScreenCapture(rect);
		final int pixels[] = ((DataBufferInt) screenImage.getData().getDataBuffer()).getData();
		long[] buffer_origin = new long[] { rect.x * Sizeof.cl_int, rect.y, 0 }; // setup initial offsets
		long[] host_origin = new long[] { 0, 0, 0 }; // screenshot data has no offsets
		long[] region = new long[] { rect.width * Sizeof.cl_int, rect.height, 1 }; // set rectangle bounds
		int result = clEnqueueWriteBufferRect(commandQueue, memObjects[0], CL_TRUE, buffer_origin, host_origin, region,
				(long) (screenRect.width * Sizeof.cl_int), (long) 0, (long) (rect.width * Sizeof.cl_int), (long) 0,
				Pointer.to(pixels), 0, null, null);
		if (result != CL_SUCCESS)
			throw new Exception(String.valueOf(result).concat(" screenshot update error occured"));
	}

	void loadFragments() {
		for (String k : Data.fragments().keySet()) {
			Frag smallFrag = Data.fragments().get(k);// get fragment
			int[] smallMatrix = flatten(smallFrag.getRgbData());// copy rgbData to
																// 1d buffer
			// load data to GPU
			cl_mem memObj = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
					Sizeof.cl_int * smallMatrix.length, Pointer.to(smallMatrix), null);
			// get pointer to data on GPU
			fragMemObjects.put(k, memObj);
		}
	}

	private void createBuffers() {
		// main screenshot array always change, so pointer is being created at a grab()
		// call
		memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_int * screenRect.width * screenRect.height, Pointer.to(new int[] { 0 }), null);
		resultFromGPUArrayPointer = Pointer.to(resultFromGPUArray);
		kernelInstrArrayPointer = Pointer.to(kernel_instruction_buffers);

		memObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_int * resultArraySize, resultFromGPUArrayPointer, null);
		memObjects[3] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_int * kernel_instruction_buffers.length, kernelInstrArrayPointer, null);

	}

	private Position[] findAll(String key) throws ScreenNotGrabbedException {
		if (screenImage == null)
			throw new ScreenNotGrabbedException();
		Frag f = Data.fragments().get(key);
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
		clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,  resultArraySize* Sizeof.cl_int,
				resultFromGPUArrayPointer, 0, null, null);
		// System.out.println(Arrays.toString(resultFromGPUArray));
		ArrayList<Position> al = null;
		// Position result = new Position();
		for (int i = 0; i < resultFromGPUArray.length && resultFromGPUArray[i] != -1; i += 2) {
			if (al == null)
				al = new ArrayList<Position>();
			al.add(new Position(resultFromGPUArray[i], resultFromGPUArray[i + 1]));
		}

		if (searchInRegion && al != null) {

			for (int i = 0; i < al.size(); i++) {
				Position frag = al.get(i);
				if (!searchRect.bounds(frag))
					al.remove(i);
			}
		}

		Position mpList[] = null;

		if (al != null)
			mpList = al.toArray(new Position[0]);
			for(Position item : mpList)
				item.add(f.center());
		return mpList;
	}

	Position find(String key) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Position[] mp = findAll(key);
		Position result = null;
		if (mp != null)
			result = mp[0];

		return result;
	}

	@Override
	public Position find(String fragName, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Position mp = find(fragName);
		if (mp != null)
			return mp.setName(customName);

		return null;
	}

	@Override
	public Position[] find_all(String fragName, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		Position mp[] = findAll(fragName);
		if (mp != null)
			for (int i = 0; i < mp.length; i++)
				mp[i].setName(customName);
		return mp;
	}

	@Override
	public Position[] find_all(String fragName) throws FragmentNotLoadedException, ScreenNotGrabbedException {
		// TODO Auto-generated method stub
		return find_all(fragName, fragName);
	}
	/*
	 * public Position[] go() { /* // Execute the kernel
	 * clEnqueueNDRangeKernel(commandQueue, kernel, 2, null, global_work_size,
	 * local_work_size, 0, null, null);
	 * 
	 * // Read the output data clEnqueueReadBuffer(commandQueue, memObjects[2],
	 * CL_TRUE, 0, resultArraySize Sizeof.cl_int, resultFromGPUArrayPointer, 0,
	 * null, null); //System.out.println(Arrays.toString(resultFromGPUArray));
	 * ArrayList al = null; // Position result = new Position(); for(int i = 0; i <
	 * resultFromGPUArray.length && resultFromGPUArray[i]!=-1; i+=2){ if(al == null)
	 * al = new ArrayList(); al.add(new Position(resultFromGPUArray[i],
	 * resultFromGPUArray[i+1])); } Position mpList[] = null; if(al != null) mpList
	 * = (Position[]) al.toArray(new Position[0]);
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

	@Override
	public Position findSimilar(String fragName, double rate, String customName)
			throws FragmentNotLoadedException, ScreenNotGrabbedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Region getSearchRect() {
		if (searchInRegion)
			return searchRect;
		else
			return null;
	}

	@Override
	public boolean getSearchInRegion() {
		return searchInRegion;
	}

	@Override
	public Rectangle getRect() {
		return screenRect;
	}

	@Override
	public void setSearchRect(int x1, int y1, int x2, int y2) {
		setSearchRect(new Region(x1, y1, x2, y2));
	}

	@Override
	public void setSearchRect(Region mr) {
		searchRect = mr;
		searchInRegion = true;
	}

	@Override
	public void setSearchRect() {
		searchInRegion = false;
	}

	@Override
	public BufferedImage getImage() {
		// TODO Auto-generated method stub
		return screenImage;
	}

	@Override
	public int getPixel(int x, int y)  {
		int result[] = new int[1];
		clEnqueueReadBuffer(commandQueue, memObjects[0], CL_TRUE, (y * screenRect.width + x) * Sizeof.cl_int,  result.length * Sizeof.cl_int,
				Pointer.to(result), 0, null, null);
		return result[0];
	}

	@Override
	public void setSearchRect(Position p1, Position p2) {
		// TODO Auto-generated method stub
		setSearchRect(new Region(p1, p2));
	}
}
