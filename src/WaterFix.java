//import java.util.*;
import java.io.File;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Iterator;

public class WaterFix extends Plugin {
	private final String name = "Water Freeze";
	private final String version = "0.1c";
	private Logger log;
	final Listener listener = new Listener();

	@Override
	public void initialize() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " initialising...");
	}

	@Override
	public void disable() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " deinitialising...");
		etc.getInstance().removeCommand("/fixwater");
	}

	@Override
	public void enable() {
		// etc.getInstance().addCommand("/drainwater",
		// "Drain nearby waterflows");
		etc.getInstance().addCommand("/fixwater", "Fix waterflows");
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this,
				PluginListener.Priority.MEDIUM);
		Logger log = Logger.getLogger("Minecraft");
		log.info(name + " " + version + " initialising...");
	}

	protected static ArrayList<Chunk> chunkList;

	class Listener extends PluginListener {

		@Override
		public boolean onCommand(Player player, String[] split) {
			if (split[0].equals("/fixwater")
					&& player.canUseCommand("/fixwater")) {
				// Do it!
				int lowChunkX = 0, lowChunkZ = 0;
				int hiChunkX = 0, hiChunkZ = 0;
				int curChunkX, curChunkZ, curChunkModX, curChunkModZ;
				curChunkModX = 0;
				curChunkModZ = 0;
				ArrayList<WaterFix.Chunk> chunkList = new ArrayList<WaterFix.Chunk>();
				// Now, iterate through X and Z for values from 0 - 63...
				// FIXME: Am guessing that world folder is called world
				File curFile;
				while (curChunkModX < 64) {
					// Does this folder exist(?)
					curFile = new File("world/"
							+ Integer.toString(curChunkModX, 36) + "/");
					if (!curFile.exists()) {
						curChunkModX++;
						continue;
					}
					curChunkModZ = 0;
					while (curChunkModZ < 64) {
						curFile = new File("world/"
								+ Integer.toString(curChunkModX, 36) + "/"
								+ Integer.toString(curChunkModZ, 36) + "/");
						if (!curFile.exists()) {
							curChunkModZ++;
							continue;
						}
						File[] fileList = curFile.listFiles();
						File a;
						int b;
						for (b = 0; b < fileList.length; b++) {
							a = fileList[b];
							if (a.getName().startsWith("c.")
									&& a.getName().endsWith(".dat")) {
								curChunkX = Integer.parseInt(
										a.getName().split("\\.")[1], 36);
								curChunkZ = Integer.parseInt(
										a.getName().split("\\.")[2], 36);
								chunkList.add(new Chunk(curChunkX, curChunkZ));
								if (hiChunkX < curChunkX)
									hiChunkX = curChunkX;
								if (lowChunkX > curChunkX)
									lowChunkX = curChunkX;
								if (hiChunkZ < curChunkZ)
									hiChunkZ = curChunkZ;
								if (lowChunkZ > curChunkZ)
									lowChunkZ = curChunkZ;
							} else
								continue;
						}
						curChunkModZ++;
					}
					curChunkModX++;
				}
				// log.info("Lowest Chunk X: " + lowChunkX + " + Z: " +
				// lowChunkZ);
				// log.info("Highest Chunk X: " + hiChunkX + " + Z: " +
				// hiChunkZ);
				double curX, maxX, minX;
				double curY, maxY, minY;
				double curZ, maxZ, minZ;
				minX = curX = lowChunkX * 16;
				minY = curY = 0;
				minZ = curZ = lowChunkZ * 16;

				maxX = hiChunkX * 16;
				maxY = 128;
				maxZ = hiChunkZ * 16;

				// log.info("Lowest Block X: " + minX + " + Z: " + minZ);
				// log.info("Highest Block X: " + maxX + " + Z: " + maxZ);
				double maxWaterY = minY;
				Block curBlock;
				curX = minX;
				int a = 0;
				int chunkListLen = chunkList.size();
				final int chunkScale = 20; // this is the number of chunks per
											// dot
				final int chunkLineScale = 40; // this is the number of dots per
												// line
				System.out.println("Processing (" + chunkListLen
						+ " chunks to go): ");
				for (Iterator<Chunk> i = chunkList.iterator(); i.hasNext();) {
					Chunk x = i.next();
					System.out.println("CHUNK HIT");
					minX = curX = x.getMinBlockX();
					minY = curY = 0;
					minZ = curZ = x.getMinBlockZ();
					maxX = x.getMaxBlockX();
					maxY = 128;
					maxZ = x.getMaxBlockZ();
					a++;
					if ((a % chunkScale) == 0)
						System.out.print("."); // dot per chunk
					if ((a % (chunkScale * chunkLineScale)) == 0) {
						System.out.println("");
						System.out.print("Processing (" + (chunkListLen - a)
								+ " chunks to go): ");
					}
					while (curX <= maxX) {
						curY = minY;
						while (curY <= maxY) {
							curZ = minZ;
							while (curZ <= maxZ) {
								/*
								 * if ((a % 10000) == 0) {
								 * log.info("Progress: "+a+"/"+maxBlock); }
								 */
								log.info("Found a block at: " + curX + ","
										+ curY + "," + curZ);
								curBlock = etc.getServer().getBlockAt(
										(int) curX, (int) curY, (int) curZ);
								// log.info("It's a <"+curBlock.getType()+">!");
								// if (curBlock.getType() == 8 ||
								// curBlock.getType() == 9) {
								if (curBlock.getType() != 0) {
									if (curBlock.getType() == 9
											|| curBlock.getType() == 8) {
										// log.info("Found a block at: " + curX
										// + "," + curY + "," + curZ);
										// etc.getServer().setBlockAt(0,
										// (int)curX, (int)curY, (int)curZ);
										// log.info("water info: " +
										// curBlock.getData());
										if (curY > maxWaterY)
											maxWaterY = curY;
										if (curBlock.getData() == 0) // source
																		// block
											continue;
										// sourceBlockList.add(curBlock); // add
										// to sourceblocklist
										etc.getServer().setBlockAt(0,
												(int) curX, (int) curY,
												(int) curZ);
									}
								}
								curZ = curZ + 1;
							}
							curY = curY + 1;
						}
						curX = curX + 1;
					}
				}
			}
			return false;
		}
	}

	private class Chunk {
		private int x;
		private int z;

		public Chunk(int newx, int newz) {
			x = newx;
			z = newz;
		}

		public int getMinBlockX() {
			return x * 16;
		}

		public int getMaxBlockX() {
			return (x + 1) * 16;
		}

		public int getMinBlockZ() {
			return z * 16;
		}

		public int getMaxBlockZ() {
			return (z + 1) * 16;
		}
	}
}
