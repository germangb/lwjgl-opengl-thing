package engine.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CvsToVoxel {

	public CvsToVoxel() {
		try {
			File file = new File("res/ammo.csv");
			Scanner scan = new Scanner(file);
			String[] dimensions = scan.nextLine().split(",");
			
			int maxX = Integer.parseInt(dimensions[0]);
			int maxY = Integer.parseInt(dimensions[1]);
			int maxZ = Integer.parseInt(dimensions[2]);
			
			int x = 0;
			int y = 0;
			int z = 0;
			while (scan.hasNext()) {
				String line = scan.nextLine();
				if (line.equals("")) {
					++y;
					z = 0;
					continue;
				}
				String[] values = line.split(",");
				x = 0;
				for (int i = 0; i < values.length; ++i) {
					String hex = values[i].substring(1);
					long num = Long.decode("0x"+hex);
					if ((num & 0xff) != 0) {
						int r = (int)((num>>24)&0xff);
						int g = (int)((num>>16)&0xff);
						int b = (int)((num>>8)&0xff);
						System.out.println("position: "+(maxZ-z-maxZ/2)+", "+(maxY-y-maxY/2)+", "+(x-maxX/2));
						System.out.println("color: "+r+", "+g+", "+b);
					}
					++x;
				}
				++z;
				
				
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
		new CvsToVoxel();
	}

}
