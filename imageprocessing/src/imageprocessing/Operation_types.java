package imageprocessing;

import java.util.Properties;

public class Operation_types
{
    public static String normal;
    public static String edge_detect;
    public static String grayscale;
    public static String binary;
    public static String blurred;
    public static String negative;
    public static String convolution;
    public static String brightnesss;
    public static String set_RGB;
    public static String change_RGB;
    public static String crop_RGB;
    public static String dilate;
    public static String erode;
    public static String deterioration;
    public static String clahe;
    public static String contrast;
    public static String ghost;

    public static String op_normal = "op_normal";
    public static String op_edge_detect = "op_edge";
    public static String op_grayscale = "op_gray";
    public static String op_binary = "op_bin";
    public static String op_blurred = "op_blur";
    public static String op_negative = "op_neg";
    public static String op_convolution = "op_conv";
    public static String op_brightnesss = "op_bright";
    public static String op_set_RGB = "op_FRGB";
    public static String op_change_RGB = "op_CRGB";
    public static String op_crop_RGB = "op_crop";
    public static String op_dilate = "op_dilate";
    public static String op_erode = "op_erode";
    public static String op_deterioration = "op_degr";
    public static String op_clahe = "op_clahe";
    public static String op_contrast = "op_contrast";
    public static String op_ghost = "op_ghost";

    public static void set_all( Properties props )
    {
        normal = props.getProperty( "ops.normal" );
        edge_detect = props.getProperty( "ops.edge_detect" );
        grayscale = props.getProperty( "ops.grayscale" );
        binary = props.getProperty( "ops.binary" );
        blurred = props.getProperty( "ops.blurred" );
        negative = props.getProperty( "ops.negative" );
        convolution = props.getProperty( "ops.convolution" );
        brightnesss = props.getProperty( "ops.brightnesss" );
        set_RGB = props.getProperty( "ops.set_RGB" );
        change_RGB = props.getProperty( "ops.change_RGB" );
        crop_RGB = props.getProperty( "ops.crop_RGB" );
        dilate = props.getProperty( "ops.dilate" );
        erode = props.getProperty( "ops.erode" );
        deterioration = props.getProperty( "ops.deterioration" );
        clahe = props.getProperty( "ops.clahe" );
        contrast = props.getProperty( "ops.contrast" );
        ghost = props.getProperty( "ops.ghost" );
    }

    public static Object[] get_all()
    {
        return new String[]
        {
            normal, edge_detect, grayscale, binary, negative, dilate, erode, deterioration, clahe, blurred, ghost, crop_RGB, brightnesss, contrast, set_RGB, change_RGB, convolution
        };
    }

}
