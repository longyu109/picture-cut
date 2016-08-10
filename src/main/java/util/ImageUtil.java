package util;

import java.io.IOException;
import java.util.ArrayList;

import magick.MagickException;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;

public class ImageUtil {

	public final static String ENCODE_PNG = "PNG";

	public final static String ENCODE_JPEG = "JPEG";

	public final static String FIRST_IMAGE = "[0]";

	/**
	 * 调整图像大小到指定的宽高
	 * 
	 * @param imagePath
	 *            原图
	 * @param toPath
	 * @param width
	 * @param height
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void resizeImage(String imagePath, String toPath, int width, int height) throws Exception {
		IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		op.addImage(imagePath);
		op.strip().resize(width, height);// sample(width, height);
		op.addImage(toPath);
		ConvertCmd converter = new ConvertCmd(true);

		// String imPath = "/usr/local/bin";
		// converter.setSearchPath(imPath);

		converter.run(op);
	}

	/**
	 * 缩放图像
	 * 
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws MagickException
	 */
	public static void scaleImage(String imagePath, String toPath, float scale) throws Exception {
		IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		op.addImage(imagePath);
		String raw = (scale * 100) + "%";
		op.strip().addRawArgs("-resize", raw);
		op.addImage(toPath);
		ConvertCmd converter = new ConvertCmd(true);

		// String imPath = "/usr/local/bin";
		// converter.setSearchPath(imPath);

		converter.run(op);
	}

	/**
	 * 根据需要的比例居中裁剪图片
	 * 
	 * @param imagePath
	 * @param toPath
	 * @param scale
	 *            裁剪比例 = width/height
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws MagickException
	 */
	public static void cropImage(String imagePath, String toPath, float scale) throws Exception {
		int[] size = getDimension(imagePath);
		int width = size[0], height = size[1];
		float currentScale = (float) width / height;
		int cutWidth = width;
		int cutHeight = height;
		if (currentScale > scale) {
			cutWidth = Math.round(scale * height);
		} else if (currentScale < scale) {
			cutHeight = Math.round(width / scale);
		}
		cropImageFromCenter(imagePath, toPath, cutWidth, cutHeight);
		// IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		// op.strip().gravity("center").extent(cutWidth, cutHeight);
		// op.addImage(toPath);
		// ConvertCmd converter = new ConvertCmd(true);
		// converter.run(op);
	}

	/**
	 * 居中裁剪一张固定大小的图片
	 * 
	 * @param imagePath
	 * @param toPath
	 * @param distWidth
	 * @param distHeight
	 * @throws Exception
	 */
	public static void cropImageFromCenter(String imagePath, String toPath, int distWidth, int distHeight)
			throws Exception {
		// IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		// op.strip().gravity("center").extent(distWidth, distHeight);
		// op.addImage(toPath);
		// ConvertCmd converter = new ConvertCmd(true);
		// converter.run(op);
		int[] size = getDimension(imagePath);
		cropImage(imagePath, toPath, (size[0] - distWidth) / 2, (size[1] - distHeight) / 2, distWidth, distHeight);
	}

	/**
	 * 裁剪
	 * 
	 * @param imagePath
	 * @param toPath
	 * @param x
	 *            右上角起点x坐标
	 * @param y
	 *            右上角起点y坐标
	 * @param width
	 *            裁剪的像素 x轴方向
	 * @param height
	 *            裁剪的像素 y轴方向
	 * @throws Exception
	 */
	public static void cropImage(String imagePath, String toPath, int x, int y, int width, int height)
			throws Exception {
		IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		op.addImage(imagePath);

		op.strip().crop(width, height, x, y);
		op.addImage(toPath);
		ConvertCmd converter = new ConvertCmd(true);

		// String imPath = "/usr/local/bin";
		// converter.setSearchPath(imPath);
		// System.out.println("======");

		converter.run(op);
		// System.out.println("crop x:" + x + ",y:" + y + ",width:" + width +
		// ",height:" + height);
	}

	public static void compressQuanlity(String imagePath, String toPath, int quanlity) throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(imagePath + FIRST_IMAGE);
		op.strip().quality((double) quanlity);
		op.addImage(toPath);
		ConvertCmd converter = new ConvertCmd(true);

		// String imPath = "/usr/local/bin";
		// converter.setSearchPath(imPath);

		converter.run(op);
	}

	public static int[] getDimension(String imagePath) throws Exception {
		IMOperation op = new IMOperation();
		op.format("%w %h");
		op.addImage(imagePath + FIRST_IMAGE);
		// op.addImage(imagePath + ENCODE_PNG);

		ArrayListOutputConsumer output = new ArrayListOutputConsumer();
		IdentifyCmd identify = new IdentifyCmd(true);
		identify.setOutputConsumer(output);

		// String imPath = "/usr/local/bin";
		// identify.setSearchPath(imPath);

		identify.run(op);

		ArrayList<String> cmdOutput = output.getOutput();
		int[] size = new int[2];
		if (null != cmdOutput && cmdOutput.size() == 1) {
			String[] outSize = cmdOutput.get(0).split(" ");
			size[0] = Integer.parseInt(outSize[0]);
			size[1] = Integer.parseInt(outSize[1]);
		}
		return size;
	}

	public static void commonResize(String imagePath, String toPath, int width, int height) throws Exception {
		int[] size = getDimension(imagePath);
		int srcWidth = size[0], srcHeight = size[1];
		float srcScale = srcWidth / (float) srcHeight;
		float scale = width / (float) height;

		if (srcScale > scale)
			srcWidth = (int) (srcHeight * scale);
		else {
			srcHeight = (int) (srcWidth / scale);
		}

		IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		op.addImage(imagePath);

		op.strip().crop(srcWidth, srcHeight);
		op.strip().gravity("center");
		op.strip().resize(width, height);

		op.addImage(toPath);
		ConvertCmd converter = new ConvertCmd(true);

		// String imPath = "/usr/local/bin";
		// converter.setSearchPath(imPath);
		// System.out.println("======");

		converter.run(op);
		// System.out.println("crop x:" + x + ",y:" + y + ",width:" + width +
		// ",height:" + height);
	}

	public static void commonCrop(String imagePath, String toPath, float scale) throws Exception {
		int[] size = getDimension(imagePath);
		int srcWidth = size[0], srcHeight = size[1];
		float srcScale = srcWidth / (float) srcHeight;

		if (srcScale > scale)
			srcWidth = (int) (srcHeight * scale);
		else {
			srcHeight = (int) (srcWidth / scale);
		}

		IMOperation op = new IMOperation();
		// op.addImage(imagePath + FIRST_IMAGE);
		op.addImage(imagePath);

		op.strip().crop(srcWidth, srcHeight);
		op.strip().gravity("center");
		op.strip().resize(srcWidth / 2, srcWidth / 2);
		op.addImage(toPath);
		ConvertCmd converter = new ConvertCmd(true);

		// String imPath = "/usr/local/bin";
		// converter.setSearchPath(imPath);
		// System.out.println("======");

		converter.run(op);
		// System.out.println("crop x:" + x + ",y:" + y + ",width:" + width +
		// ",height:" + height);
	}

	public static void main(String[] args) {
		// String src = "/Users/xuxiyuan/Downloads/image/tfboy.jpg";
		// String dst = "/Users/xuxiyuan/Downloads/image/";
		String src = "/Users/nali/Desktop/a.png";
		String dst = "/Users/nali/Desktop/b1.png";

		// String src = "/Users/nali/Desktop/1.jpeg";
		// String dst = "/Users/nali/Desktop/4.png";

		// String src = "/Users/nali/Desktop/1.png";
		// String dst = "/Users/nali/Desktop/7.png";
		try {
			// int[] dimension = getDimension(src);
			// System.out.println("w:" + dimension[0] + ",h:" + dimension[1]);
			// cropImageFromCenter(src, dst, 400, 400);
			// resizeImage(src, dst + "tfboy_86.jpg", 86, 86);
			// scaleImage(src, dst + "tfboy_scale.jpg", 0.14f);

			// resizeImage(src, dst, 100, 100);
			// scaleImage(src, dst, 0.5f);
			// cropImage(src, dst, 1.0f);
			int[] size = getDimension(src);
			System.out.println("w:" + size[0] + ",h:" + size[1]);
			// commonCrop(src, dst, 1.0f);
			commonResize(src, dst, 640, 640);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
