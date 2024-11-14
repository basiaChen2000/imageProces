// 7111029062 陳思蓓 
// 2022/12/09
// 影像期末


/*  原圖 -> {1_灰階}
灰階 -> {2_負片，3_Gamma>1，4_Gamma<1，5_對比拉開}
Gamma>1 -> {6_二值化}
Gamma<1 -> {7_胡椒鹽雜訊}
胡椒鹽雜訊 -> {8_3X3中值濾波器}
對比拉開 -> {9_Laplacian}
Laplacian -> {10_3X3最大濾波器}
*/

// 套件
import javax.imageio.ImageIO;
import javax.management.ValueExp;

// import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;


public class ImageProcess{
    public static void main(String args[]){
        //讀取檔案
        BufferedImage Image_read;
        try{
            Image_read = ImageIO.read(new File("C.jpg"));

            // 1__灰階
            BufferedImage grayImg = Gray(Image_read);

            // 2_負片
            Negative(grayImg);

            // 3_Gramma < 1
            BufferedImage gamma0 = Gamma(grayImg,0.5,3);

            // 4_Gramma = 1
            BufferedImage gamma1 = Gamma(grayImg,1,4);

            // 5_Gramma > 1
            BufferedImage gamma2 = Gamma(grayImg,2,5);
            
            // 6_二值化
            OTSU(gamma2);

            // 7_胡椒鹽雜訊
            BufferedImage saltPepperImg = SaltPepper(gamma0);

            // 8_3X3中值濾波器
            Median(saltPepperImg);

            // 9_Laplacian
            BufferedImage laplacianImg = Laplacian(gamma1);

            // 10_3X3最大濾波器
            maxFilter(laplacianImg);
            
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    // 最大濾波器
    public static void maxFilter(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());

        for (int i = 1; i < img.getWidth()-1; i++) {
            for (int j = 1; j < img.getHeight()-1; j++) {
                // 原圖
                // 儲存3X3的pixel
                int[] sortPiexl = new int[9];
                sortPiexl[0] = img.getRGB(i-1, j-1) & 0xff;
                sortPiexl[1] = img.getRGB(i, j-1) & 0xff;
                sortPiexl[2] = img.getRGB(i+1, j-1) & 0xff;
                sortPiexl[3] = img.getRGB(i-1, j) & 0xff;
                sortPiexl[4] = img.getRGB(i, j) & 0xff;
                sortPiexl[5] = img.getRGB(i+1, j) & 0xff;
                sortPiexl[6] = img.getRGB(i-1, j+1) & 0xff;
                sortPiexl[7] = img.getRGB(i, j+1) & 0xff;
                sortPiexl[8] = img.getRGB(i+1, j+1) & 0xff;

                sort(sortPiexl);

                // 檢查Max
                int color = img.getRGB(i, j);
                if(sortPiexl[8] > color){
                    color = sortPiexl[8];
                }
                int newPixel = color;
                newPixel = colorToRGB(newPixel, newPixel, newPixel);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("10_最大濾波器.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 最大濾波器 : 10_最大濾波器.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lapcian 3X3 三微
    public static int Lpc_3(int[][] matrix){
        int value = 0;
        int[][] mask = {{1,-2,1},{-2,4,-2},{1,-2,1}};
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                value += matrix[i][j]  *  mask[i][j];
            }
        }
        if(value > 255){
            value = 255;
        }else if(value < 0){
            value = 0;
        }
        return value;
    }

    // Lapcian 3X3 二微
    public static int Lpc_2(int[][] matrix){
        int value = 0;
        int[][] mask = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                value += matrix[i][j]  *  mask[i][j];
            }
        }
        // 檢查像素是否在合理範圍
        if(value > 255){
            value = 255;
        }else if(value < 0){
            value = 0;
        }
        return value;
    }

    // Lapcian 3X3 一微
    public static int Lpc_1(int[][] matrix){
        int value = 0;
        int[][] mask = {{0,-1,0},{-1,4,-1},{0,-1,0}};
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                value += matrix[i][j]  *  mask[i][j];
            }
        }
        if(value > 255){
            value = 255;
        }else if(value < 0){
            value = 0;
        }
        return value;
    }

    // Laplacian
    public static BufferedImage Laplacian(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());

        for (int i = 1; i < img.getWidth()-1; i++) {
            for (int j = 1; j < img.getHeight()-1; j++) {
                // 原圖
                // 儲存3X3的pixel
                int[][] matrix = new int[3][3];
                matrix[0][0] = img.getRGB(i-1, j-1) & 0xff;
                matrix[0][1] = img.getRGB(i, j-1) & 0xff;
                matrix[0][2] = img.getRGB(i+1, j-1) & 0xff;
                matrix[1][0] = img.getRGB(i-1, j) & 0xff;
                matrix[1][1] = img.getRGB(i, j) & 0xff;
                matrix[1][2] = img.getRGB(i+1, j) & 0xff;
                matrix[2][0] = img.getRGB(i-1, j+1) & 0xff;
                matrix[2][1] = img.getRGB(i, j+1) & 0xff;
                matrix[2][2] = img.getRGB(i+1, j+1) & 0xff;

                // 計算
                int newPixel = Lpc_1(matrix);

                newPixel = colorToRGB(newPixel, newPixel, newPixel);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("9_Laplacian.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 Laplacian : 9_Laplacian.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output_Image;
    }

    // 由小到大排序
    public static int[] sort(int[] array){
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j < array.length; j++){
                if(array[i] < array[j]){
                    int tmp = array[i];
                    array[i] = array[j];
                    array[j] = tmp;
                }
            }
        }
        return array;
    }

    // 中位數(去雜訊)
    public static BufferedImage Median(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());

        for (int i = 1; i < img.getWidth()-1; i++) {
            for (int j = 1; j < img.getHeight()-1; j++) {
                // 原圖
                // 儲存3X3的pixel
                int[] sortPiexl = new int[9];
                sortPiexl[0] = img.getRGB(i-1, j-1) & 0xff;
                sortPiexl[1] = img.getRGB(i, j-1) & 0xff;
                sortPiexl[2] = img.getRGB(i+1, j-1) & 0xff;
                sortPiexl[3] = img.getRGB(i-1, j) & 0xff;
                sortPiexl[4] = img.getRGB(i, j) & 0xff;
                sortPiexl[5] = img.getRGB(i+1, j) & 0xff;
                sortPiexl[6] = img.getRGB(i-1, j+1) & 0xff;
                sortPiexl[7] = img.getRGB(i, j+1) & 0xff;
                sortPiexl[8] = img.getRGB(i+1, j+1) & 0xff;

                sort(sortPiexl);

                // 選擇要製造雜訊的位置
                int newPixel = sortPiexl[4];
                newPixel = colorToRGB(newPixel, newPixel, newPixel);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("8_Median.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 中值濾波器 : 8_Median.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output_Image;
    }

    // 胡椒鹽
    public static BufferedImage SaltPepper(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());

        // 散播黑白點
        Random random = new Random();
        // 用來設定雜訊多寡
        int rPrmt = (int)(img.getWidth()/5);
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                // 原圖
                int color = img.getRGB(i, j);

                // 選擇要製造雜訊的位置
                int pepper = random.nextInt(rPrmt);
                if (pepper == rPrmt-1) {
                    pepper = 255;
                }else if (pepper == 0){
                    pepper = 0;
                }else{
                    // 未被選中的維持原像素
                    pepper = color & 0xff;
                }
                int newPixel = colorToRGB(pepper, pepper, pepper);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("7_SaltPepper.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 胡椒鹽雜訊 : 7_SaltPepper.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output_Image;
    }

    // 二值化
    public static void OTSU(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        int avg_pixel = 0;
        // 先算出整張圖的平均
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);
                int pixel = color & 0xff;
                avg_pixel += pixel;
            }
        }
        avg_pixel = (int)(avg_pixel/(img.getWidth() * img.getHeight()));

        // 依照門檻更改值
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);
                int pixel = color & 0xff;
                int newPixel;
                if(pixel >= avg_pixel){
                    newPixel = colorToRGB(255, 255, 255);
                }else{
                    newPixel = colorToRGB(0, 0, 0);
                }
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("6_OTSU.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 二值化 : 6_OTSU.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 不同Gamma的計算
    // num 用來顯示第幾張輸出的照片，與影像處理無關
    public static BufferedImage GammaCalculate(BufferedImage img,int max,int min,double gammaValue,int num){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);
                int pixel = color & 0xff;
                pixel = (int)(Math.pow((double)(pixel - min) / (max - min),gammaValue) * 255);
                int newPixel = colorToRGB(pixel, pixel, pixel);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File( num + "_Gamma(" + gammaValue + ").jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生: Gamma : " + num + "_Gamma(" + gammaValue + ").jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output_Image;
    }

    // Gamma (0.5，2)、Gamma(1)=對比拉伸
    public static BufferedImage Gamma(BufferedImage img,double gammavalue,int num){
        // 尋找整張照片最大及最小Pixel
        int min_Pixel = 255;
        int max_Pixel = 0;
        for (int i = 0; i < img.getWidth(); i++) {
            // 先找min/max
            for (int j = 0; j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);
                int ImgPixel = color & 0xff;
                // 檢查min/max
                if (ImgPixel < min_Pixel){
                    min_Pixel = ImgPixel;
                }
                if (ImgPixel > max_Pixel){
                    max_Pixel = ImgPixel;
                }
            }
        }

        return  GammaCalculate(img,max_Pixel,min_Pixel,gammavalue,num);
        
    }    


    // 負片
    public static void Negative(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);
                int pixel = color & 0xff;
                int newPixel = colorToRGB(255-pixel, 255-pixel, 255-pixel);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("2_Negative.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 負片 : 2_Negative.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 灰階
    public static BufferedImage Gray(BufferedImage img){
        BufferedImage output_Image;
        output_Image = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int color = img.getRGB(i, j);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                int newPixel = colorToRGB(gray, gray, gray);
                output_Image.setRGB(i, j, newPixel);
            }
        }
        try {
            File outputfile = new File("1_Gray.jpg");
            ImageIO.write(output_Image, "jpg",outputfile);
            System.out.println("產生 灰階 : 1_Gray.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output_Image;
    }

    // rgb存回pixel
    private static int colorToRGB(int red, int green, int blue) {
        int newPixel = red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

	}
    
    

}