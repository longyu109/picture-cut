package util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;

import magick.ColorspaceType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.QuantizeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmagickUtil {

    private final static Logger log = LoggerFactory.getLogger(JmagickUtil.class);
    static {
        System.setProperty("jmagick.systemclassloader", "no");
    }

    public final static String ENCODE_PNG = "PNG";

    public final static String ENCODE_JPEG = "JPEG";

    /**
     * 调整图像大小到指定的宽高
     * @param imagePath 原图
     * @param toPath 
     * @param width
     * @param height
     */
    public static void resizeImage(String imagePath, String toPath, int width, int height) throws MagickException {
        MagickImage image = null;
        MagickImage scaleImg = null;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            image = new MagickImage(info);
            String originImageFormat = image.getImageFormat();
            scaleImg = image.scaleImage(width, height);
            // clear all the metadata
            scaleImg.profileImage("*", null);
            scaleImg.setFileName(toPath);
            if (!originImageFormat.equalsIgnoreCase(ENCODE_JPEG) && !originImageFormat.equalsIgnoreCase(ENCODE_PNG)) {
                scaleImg.setImageFormat(ENCODE_JPEG);
            }
            scaleImg.writeImage(info);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new MagickException(e.getMessage());
        } finally {
            if (null != image) {
                image.destroyImages();
            }
            if (null != scaleImg) {
                scaleImg.destroyImages();
            }
        }
    }

    /**
     * 缩放图像
     * @throws MagickException 
     */
    public static void scaleImage(String imagePath, String toPath, float scale) throws MagickException {
        MagickImage image = null;
        int toWidth, toHeight;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            image = new MagickImage(info);
            Dimension imageDim = image.getDimension();
            int width = imageDim.width;
            int height = imageDim.height;
            toWidth = Math.round(width * scale);
            toHeight = Math.round(height * scale);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new MagickException(e.getMessage());
        } finally {
            if (null != image) {
                image.destroyImages();
            }
        }
        resizeImage(imagePath, toPath, toWidth, toHeight);
    }

    /**
     * 根据需要的比例居中裁剪图片
     * 
     * @param imagePath
     * @param toPath
     * @param scale 裁剪比例 = width/height
     * @throws MagickException 
     */
    public static void cropImage(String imagePath, String toPath, float scale) throws MagickException {
        MagickImage image = null;
        int srcCutX, srcCutY, cutWidth, cutHeight;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            image = new MagickImage(info);
            Dimension imageDim = image.getDimension();
            int width = imageDim.width;
            int height = imageDim.height;

            float currentScale = (float) width / height;
            cutWidth = width;
            cutHeight = height;
            if (currentScale > scale) {
                cutWidth = Math.round(scale * height);
            } else if (currentScale < scale) {
                cutHeight = Math.round(width / scale);
            }
            srcCutX = Math.round((float) ((width - cutWidth) / 2.0));
            srcCutY = Math.round((float) ((height - cutHeight) / 2.0));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new MagickException(e.getMessage());
        } finally {
            if (null != image) {
                image.destroyImages();
            }
        }
        cropImage(imagePath, toPath, srcCutX, srcCutY, cutWidth, cutHeight);

    }

    public static void cropImageWithPadding(String imagePath, String toPath, int distWidth, int distHeight)
            throws MagickException {
        MagickImage image = null;
        int width, height;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            image = new MagickImage(info);
            Dimension imageDim = image.getDimension();
            width = imageDim.width;
            height = imageDim.height;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new MagickException(e.getMessage());
        } finally {
            if (null != image) {
                image.destroyImages();
            }
        }
        cropImage(imagePath, toPath, Math.round((float) ((width - distWidth) / 2.0)),
                Math.round((float) ((height - distHeight) / 2.0)), width, height);

    }

    public static void cropImage(String imagePath, String toPath, int x, int y, int width, int height)
            throws MagickException {
        MagickImage image = null;
        MagickImage cropped = null;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            image = new MagickImage(info);
            String originImageFormat = image.getImageFormat();
            Rectangle rct = new Rectangle(x, y, width, height);
            cropped = image.cropImage(rct);
            // clear all the metadata
            cropped.profileImage("*", null);
            cropped.setFileName(toPath);
            if (!originImageFormat.equalsIgnoreCase(ENCODE_JPEG) && !originImageFormat.equalsIgnoreCase(ENCODE_PNG)) {
                cropped.setImageFormat(ENCODE_JPEG);
            }
            cropped.writeImage(info);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new MagickException(e.getMessage());
        } finally {
            if (null != image) {
                image.destroyImages();
            }
            if (null != cropped) {
                cropped.destroyImages();
            }
        }
    }

    public static void compressQuanlity(String imagePath, String toPath, int quanlity) {
        MagickImage compress = null;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            info.setQuality(50);
            compress = new MagickImage(info);
            compress.profileImage("*", null);
            compress.setFileName(toPath);
            compress.writeImage(info);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static int[] getDimension(String imagePath) {
        int[] dimension = new int[2];
        MagickImage image = null;
        try {
            ImageInfo info = new ImageInfo(imagePath);
            image = new MagickImage(info);
            dimension[0] = (int) image.getDimension().getWidth();
            dimension[1] = (int) image.getDimension().getHeight();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return dimension;
    }

    protected void setImageQuanlity(MagickImage image) {
        try {
            QuantizeInfo quantizeInfo = new QuantizeInfo();
            quantizeInfo.setColorspace(ColorspaceType.RGBColorspace);//XYZColorspace
            quantizeInfo.setNumberColors(8);
            quantizeInfo.setTreeDepth(1);
            quantizeInfo.setColorspace(0);
            quantizeInfo.setDither(0);
            image.quantizeImage(quantizeInfo);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        String imagePath = "/Users/xuxiyuan/Downloads/0930070.png";
        File file = new File(imagePath);
        if (!file.exists()) {
            System.out.println("image do not exsit:" + imagePath);
            System.exit(0);
        }
        try {
            System.out.println("======get dimension======");
            ImageInfo info = new ImageInfo(imagePath);
            System.out.println("======info====");
            MagickImage image = new MagickImage(info);
            System.out.print("====image=====");
            double width = image.getDimension().getWidth();
            System.out.print("====width:" + width);
            double height = image.getDimension().getWidth();
            System.out.println("image width:" + image.getDimension().getWidth() + ",height:" + height);

            System.out.println("=========crop image========");
            String toPath = imagePath + ".0.5.png";
            cropImage(imagePath, toPath, 0.5f);
            System.out.println("======crop 0.5 scale to path : " + toPath);
            String toPath2 = imagePath + ".d2.png";
            cropImage(imagePath, toPath, 0, 0, (int) width / 2, (int) height / 2);
            System.out.println("====crop 1/2 to path:" + toPath2);

            System.out.println("======resize image 4");
            String toPath3 = imagePath + ".resize_4.png";
            resizeImage(imagePath, toPath2, (int) width / 4, (int) height);
            System.out.println("======resize image 4 to path: " + toPath3);
        } catch (MagickException e) {
            e.printStackTrace();
            System.out.println("catch magic exception");
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("======catch throwable");
        }
    }
}
