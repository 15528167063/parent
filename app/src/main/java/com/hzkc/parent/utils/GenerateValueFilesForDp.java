package com.hzkc.parent.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhy on 15/5/3.
 * 鸿洋大神自动生层dimens文件（用于dp格式的适配）
 * https://github.com/hongyangAndroid/Android_Blog_Demos/tree/master/blogcodes/src/main/java/com/zhy/blogcodes/genvalues
 */
public class GenerateValueFilesForDp {

    //以320dp为基础
    private final static int baseDp = 360;
//    private int baseH;

    private String dirStr = "./res/commdp";

    private final static String WTemplate = "<dimen name=\"h_{0}\">{1}dp</dimen>\n";
    public static final String W_TEMPLATE = WTemplate;
    private final static String HTemplate = "<dimen name=\"v_{0}\">{1}dp</dimen>\n";

    //字体
    private final static String FONT_TEMPLATE = "<dimen name=\"text_{0}\">{1}sp</dimen>\n";
    private static Map<String, Float> font = new HashMap();

    static {
        font.put("XXX_larger", 36f);
        font.put("XX_larger", 22f);
        font.put("Increase_larger", 16.5f);
        font.put("larger", 15f);
        font.put("medium", 14f);
        font.put("mini", 12f);
        font.put("least_mini", 10f);
    }

    /**
     * {0}-HEIGHT
     */
    private final static String VALUE_TEMPLATE = "values-w{0}dp";

    private static final String SUPPORT_DIMESION = "240;320;360;400;420;450;480";

    private String supportStr = SUPPORT_DIMESION;
    public static final StringBuffer SB_FOR_WIDTH = new StringBuffer();

    public GenerateValueFilesForDp(String supportStr) {
//        this.baseDp = baseDp;
//        this.baseH = baseY;

        if (!this.supportStr.contains(String.valueOf(baseDp))) {
            this.supportStr += ";" + baseDp;
        }

        this.supportStr += validateInput(supportStr);

        System.out.println(supportStr);

        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdir();

        }
        System.out.println(dir.getAbsoluteFile());

    }

    /**
     * @param supportStr
     *            w,h_...w,h;
     * @return
     */
    private String validateInput(String supportStr) {
        StringBuffer sb = new StringBuffer();
        String[] vals = supportStr.split("_");
        int w = -1;
        int h = -1;
        String[] wh;
        for (String val : vals) {
            try {
                if (val == null || val.trim().length() == 0)
                    continue;

                wh = val.split(",");
                w = Integer.parseInt(wh[0]);
                h = Integer.parseInt(wh[1]);
            } catch (Exception e) {
                System.out.println("skip invalidate params : w,h = " + val);
                continue;
            }
            sb.append(w + "," + h + ";");
        }

        return sb.toString();
    }

    public void generate() {
        String[] vals = supportStr.split(";");
        for (String val : vals) {
            String[] wh = val.split(",");
            generateXmlFile(Integer.parseInt(wh[0]));
//            generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
        }

    }

    /**
     * @param val eg. 240;320;360...
     */
    private void generateXmlFile(int val) {

        SB_FOR_WIDTH.setLength(0);

        SB_FOR_WIDTH.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        SB_FOR_WIDTH.append("<resources>\n");
        float cellw = val * 1.0f / baseDp;

        System.out.println("width : " + val + "," + baseDp + "," + cellw);
        //0.5dp
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "0_5").replace("{1}", change(cellw * 0.5f) + ""));

        //最大统计到200dp
        int max = 200;

        for (int i = 1; i < max; i++) {
            SB_FOR_WIDTH.append(WTemplate.replace("{0}", i + "").replace("{1}", change(cellw * i) + ""));
        }
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", max + "").replace("{1}", change(cellw * max) + ""));

        //特殊情况-10dp、 -20dp
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "10_").replace("{1}", change(cellw * (-10)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "20_").replace("{1}", change(cellw * (-20)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "240").replace("{1}", change(cellw * (240)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "300").replace("{1}", change(cellw * (300)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "400").replace("{1}", change(cellw * (400)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "450").replace("{1}", change(cellw * (450)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "600").replace("{1}", change(cellw * (600)) + ""));
        SB_FOR_WIDTH.append(WTemplate.replace("{0}", "800").replace("{1}", change(cellw * (800)) + ""));

        SB_FOR_WIDTH.append("</resources>");


        //纵向
        StringBuffer sbForHeight = new StringBuffer();
        sbForHeight.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForHeight.append("<resources>\n");
        float cellh = val *1.0f/ baseDp;
        System.out.println("height : "+ val + "," + baseDp + "," + cellh);
        //0.5dp
        sbForHeight.append(HTemplate.replace("{0}", "0_5").replace("{1}", change(cellw * 0.5f) + ""));
        for (int i = 1; i < max; i++) {
            sbForHeight.append(HTemplate.replace("{0}", i + "").replace("{1}",change(cellh * i) + ""));
        }
        sbForHeight.append(HTemplate.replace("{0}", max + "").replace("{1}", change(cellh * max) + ""));

        //特殊情况-10dp、 -20dp
        sbForHeight.append(HTemplate.replace("{0}", "10_").replace("{1}", change(cellw * (-10)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "20_").replace("{1}", change(cellw * (-20)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "240").replace("{1}", change(cellw * (240)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "300").replace("{1}", change(cellw * (300)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "400").replace("{1}", change(cellw * (400)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "450").replace("{1}", change(cellw * (450)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "600").replace("{1}", change(cellw * (600)) + ""));
        sbForHeight.append(HTemplate.replace("{0}", "800").replace("{1}", change(cellw * (800)) + ""));
        sbForHeight.append("</resources>");

        //字体
        StringBuffer sbForFont = new StringBuffer();
        sbForFont.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForFont.append("<resources>\n");
//        float cellh = val *1.0f/ baseDp;
        System.out.println("height : "+ val + "," + baseDp + "," + cellh + "font = " + font.size() + "  \n " +  font);
        Set<String> strings = font.keySet();
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Float value = font.get(key);
            sbForFont.append(FONT_TEMPLATE.replace("{0}", key).replace("{1}", change(cellw * value) + ""));
        }
        sbForFont.append("</resources>");


        //写入到文件
        File fileDir = new File(dirStr + File.separator
                + VALUE_TEMPLATE.replace("{0}", val + ""));//
//                .replace("{1}", w + ""));
        fileDir.mkdir();


        File layxFile = new File(fileDir.getAbsolutePath(), "lay_h.xml");
        File layyFile = new File(fileDir.getAbsolutePath(), "lay_v.xml");
        File fontFile = new File(fileDir.getAbsolutePath(), "font.xml");
        try {
            //写x
            PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
            pw.print(SB_FOR_WIDTH.toString());
            pw.close();
            //写y
            pw = new PrintWriter(new FileOutputStream(layyFile));
            pw.print(sbForHeight.toString());
            pw.close();
            //写common
            pw = new PrintWriter(new FileOutputStream(fontFile));
            pw.print(sbForFont.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }

    public static void main(String[] args) {
        int baseW = 320;
        int baseH = 480;
        String addition = "";
        try {
            if (args.length >= 3) {
                baseW = Integer.parseInt(args[0]);
                baseH = Integer.parseInt(args[1]);
                addition = args[2];
            } else if (args.length >= 2) {
                baseW = Integer.parseInt(args[0]);
                baseH = Integer.parseInt(args[1]);
            } else if (args.length >= 1) {
                addition = args[0];
            }
        } catch (NumberFormatException e) {

            System.err
                    .println("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("baseDp  = " + baseW + ", baseH = " + baseH +
                ",  addition = " + addition);
        new GenerateValueFilesForDp(addition).generate();

    }

}
