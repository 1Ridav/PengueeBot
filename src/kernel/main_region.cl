__kernel void findAll(__global const int *a, __global const int *b, __global int *c, __global int *kernelInstrArrayPointer){
   		int x = get_global_id(0);
 		int y = get_global_id(1);
 		int p1_x = kernelInstrArrayPointer[5];
 		int p1_y = kernelInstrArrayPointer[6];
 		int p2_x = kernelInstrArrayPointer[7];
 		int p2_y = kernelInstrArrayPointer[8];
 		if(x >= p1_x && x <= p2_x && y >= p1_y && y <= p2_y)
 		int widthBig = kernelInstrArrayPointer[0]; //optimize for lucky future
 
     	if( a[y * widthBig + x] == b[0]){ //if first pixel found, then check the rest
 			int cacheSmall, cacheBig;
 			int heightBig = 	kernelInstrArrayPointer[1];
 			int widthSmall = 	kernelInstrArrayPointer[2];
 			int heightSmall = 	kernelInstrArrayPointer[3];
 
 			for(int i = 0; i < heightSmall; i++){ //check Y, prepare cached calculations 
 				cacheSmall = i * widthSmall;
 				cacheBig = (y+i) * widthBig + x;
 				for(int j = 0; j < widthSmall; j++){ //check X
 					if( a[cacheBig + j] != b[cacheSmall + j]){
 						return;
 					}
 				}
 			}

 			atomic_add(&kernelInstrArrayPointer[4], 2);
 			int p = kernelInstrArrayPointer[4];
 			c[p] = x;
 			c[p+1] = y;
		}
}