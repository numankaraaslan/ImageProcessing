package imageprocessing;

public class Operation
{
    private final int operation_number;
    private String op_name;
    private int crop_low, crop_high, set_R, set_G, set_B, brightnesss = 0, blur_size = 40, clahe_limit = 40, clahe_size = 8, deterioration = 40, change_R, change_G, change_B;
    private double contrast = 1.0;
    private double[][] convolution = new double[][]
    {
        new double[]
        {
            0, 0, 0
        },
        new double[]
        {
            0, 0, 0
        },
        new double[]
        {
            0, 0, 0
        }
    };

    public int get_blur_size()
    {
        return blur_size;
    }

    public void set_blur_size( int blur_size )
    {
        this.blur_size = blur_size;
    }

    public Operation( int i )
    {
        this.operation_number = i;
        op_name = Operation_types.normal;
    }

    public int get_operation_number()
    {
        return operation_number;
    }

    public void set_convolution( double[][] convolution )
    {
        this.convolution = convolution;
    }

    public double[][] get_convolution()
    {
        return convolution;
    }

    public void set_brightnesss( int brightnesss )
    {
        this.brightnesss = brightnesss;
    }

    public int get_brightnesss()
    {
        return brightnesss;
    }

    public int[] get_RGB_values()
    {
        return new int[]
        {
            set_R, set_G, set_B
        };
    }

    public void set_RGB_values( int set_R, int set_G, int set_B )
    {
        this.set_R = set_R;
        this.set_G = set_G;
        this.set_B = set_B;
    }

    public int[] get_change_RGB_values()
    {
        return new int[]
        {
            change_R, change_G, change_B
        };
    }

    public void change_RGB_values( int change_R, int change_G, int change_B )
    {
        this.change_R = change_R;
        this.change_G = change_G;
        this.change_B = change_B;
    }

    public int[] get_crop_values()
    {
        return new int[]
        {
            crop_low, crop_high
        };
    }

    public void set_crop_values( int crop_low, int crop_high )
    {
        this.crop_low = crop_low;
        this.crop_high = crop_high;
    }

    public int[] get_clahe_values()
    {
        return new int[]
        {
            clahe_limit, clahe_size
        };
    }

    public void set_clahe_values( int clahe_limit, int clahe_size )
    {
        this.clahe_limit = clahe_limit;
        this.clahe_size = clahe_size;
    }

    public String get_op_name()
    {
        return op_name;
    }

    public void set_op_name( String op_name )
    {
        this.op_name = op_name;
    }

    public double get_contrast()
    {
        return this.contrast;
    }

    public void set_contrast( double contrast )
    {
        this.contrast = contrast;
    }

    public void set_deterioration( int deterioration )
    {
        this.deterioration = deterioration;
    }

    public int get_deterioration()
    {
        return deterioration;
    }

}
