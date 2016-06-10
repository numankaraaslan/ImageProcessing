package imageprocessing;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Operator
{
    public static int processors;
    public static Deteriorationthread[] threads;

    public static Mat do_operations( Mat currentFrame, Iterable<Operation> operations )
    {
        for ( Operation some_operation : operations )
        {
            if ( some_operation.get_op_name().equals( Operation_types.binary ) )
            {
                if ( currentFrame.channels() != 1 )
                {
                    Imgproc.cvtColor( currentFrame, currentFrame, Imgproc.COLOR_BGR2GRAY );
                }
                Imgproc.threshold( currentFrame, currentFrame, 127, 255, Imgproc.THRESH_BINARY );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.blurred ) )
            {
                Imgproc.blur( currentFrame, currentFrame, new Size( some_operation.get_blur_size(), some_operation.get_blur_size() ) );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.deterioration ) )
            {
                if ( currentFrame.channels() != 1 )
                {
                    thread_work( currentFrame, some_operation.get_deterioration() );
                }
            }
            else if ( some_operation.get_op_name().equals( Operation_types.brightnesss ) )
            {
                currentFrame.convertTo( currentFrame, currentFrame.type(), 1, some_operation.get_brightnesss() );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.contrast ) )
            {
                currentFrame.convertTo( currentFrame, -1, some_operation.get_contrast(), 0 );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.convolution ) )
            {
                double[][] convolution_kernel = some_operation.get_convolution();
                Mat kernel = new Mat( new Size( 3, 3 ), 1 );
                kernel.put( 0, 0, convolution_kernel[0][0] );
                kernel.put( 0, 1, convolution_kernel[0][1] );
                kernel.put( 0, 2, convolution_kernel[0][2] );
                kernel.put( 1, 0, convolution_kernel[1][0] );
                kernel.put( 1, 1, convolution_kernel[1][1] );
                kernel.put( 1, 2, convolution_kernel[1][2] );
                kernel.put( 2, 0, convolution_kernel[2][0] );
                kernel.put( 2, 1, convolution_kernel[2][1] );
                kernel.put( 2, 2, convolution_kernel[2][2] );
                Imgproc.filter2D( currentFrame, currentFrame, currentFrame.depth(), kernel );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.crop_RGB ) )
            {
                Imgproc.threshold( currentFrame, currentFrame, some_operation.get_crop_values()[1], some_operation.get_crop_values()[1], Imgproc.THRESH_TRUNC );
                Core.absdiff( currentFrame, new Mat( currentFrame.rows(), currentFrame.cols(), currentFrame.type(), new Scalar( 255, 255, 255 ) ), currentFrame );
                Imgproc.threshold( currentFrame, currentFrame, 255 - some_operation.get_crop_values()[0], 255 - some_operation.get_crop_values()[0], Imgproc.THRESH_TRUNC );
                Core.absdiff( currentFrame, new Mat( currentFrame.rows(), currentFrame.cols(), currentFrame.type(), new Scalar( 255, 255, 255 ) ), currentFrame );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.edge_detect ) )
            {
                Mat kernel = new Mat( new Size( 3, 3 ), 1 );
                kernel.put( 0, 0, 1 );
                kernel.put( 0, 1, 0 );
                kernel.put( 0, 2, -1 );
                kernel.put( 1, 0, 1.5 );
                kernel.put( 1, 1, 0 );
                kernel.put( 1, 2, -1.5 );
                kernel.put( 2, 0, 1 );
                kernel.put( 2, 1, 0 );
                kernel.put( 2, 2, -1 );
                Mat mat_1 = new Mat();
                Imgproc.filter2D( currentFrame, mat_1, currentFrame.depth(), kernel );
                kernel = new Mat( new Size( 3, 3 ), 1 );
                kernel.put( 0, 0, -1 );
                kernel.put( 0, 1, -1.5 );
                kernel.put( 0, 2, -1 );
                kernel.put( 1, 0, 0 );
                kernel.put( 1, 1, 0 );
                kernel.put( 1, 2, 0 );
                kernel.put( 2, 0, 1 );
                kernel.put( 2, 1, 1.5 );
                kernel.put( 2, 2, 1 );
                Mat mat_2 = new Mat();
                Imgproc.filter2D( currentFrame, mat_2, currentFrame.depth(), kernel );
                Core.add( mat_1, mat_2, currentFrame );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.grayscale ) )
            {
                if ( currentFrame.channels() != 1 )
                {
                    Imgproc.cvtColor( currentFrame, currentFrame, Imgproc.COLOR_BGR2GRAY );
                }
            }
            else if ( some_operation.get_op_name().equals( Operation_types.negative ) )
            {
                Core.absdiff( currentFrame, new Mat( currentFrame.rows(), currentFrame.cols(), currentFrame.type(), new Scalar( 255, 255, 255 ) ), currentFrame );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.dilate ) )
            {
                Imgproc.dilate( currentFrame, currentFrame, Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE, new Size( 4, 4 ) ) );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.erode ) )
            {
                Imgproc.erode( currentFrame, currentFrame, Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE, new Size( 4, 4 ) ) );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.clahe ) )
            {
                if ( currentFrame.channels() != 1 )
                {
                    Imgproc.cvtColor( currentFrame, currentFrame, Imgproc.COLOR_BGR2GRAY );
                }
                Imgproc.createCLAHE( some_operation.get_clahe_values()[0], new Size( some_operation.get_clahe_values()[1], some_operation.get_clahe_values()[1] ) ).apply( currentFrame, currentFrame );
            }
            else if ( some_operation.get_op_name().equals( Operation_types.set_RGB ) )
            {
                if ( currentFrame.channels() != 1 )
                {
                    int R_value = some_operation.get_RGB_values()[0];
                    int G_value = some_operation.get_RGB_values()[1];
                    int B_value = some_operation.get_RGB_values()[2];
                    List<Mat> split_mats = new ArrayList<>( 3 );
                    Core.split( currentFrame, split_mats );
                    if ( B_value >= 0 )
                    {
                        split_mats.get( 0 ).setTo( new Scalar( B_value ) );
                    }
                    if ( G_value >= 0 )
                    {
                        split_mats.get( 1 ).setTo( new Scalar( G_value ) );
                    }
                    if ( R_value >= 0 )
                    {
                        split_mats.get( 2 ).setTo( new Scalar( R_value ) );
                    }
                    Core.merge( split_mats, currentFrame );
                }
            }
            else if ( some_operation.get_op_name().equals( Operation_types.change_RGB ) )
            {
                if ( currentFrame.channels() != 1 )
                {
                    int R_value = some_operation.get_change_RGB_values()[0];
                    int G_value = some_operation.get_change_RGB_values()[1];
                    int B_value = some_operation.get_change_RGB_values()[2];
                    List<Mat> split_mats = new ArrayList<>( 3 );
                    Core.split( currentFrame, split_mats );
                    Core.add( split_mats.get( 0 ), new Scalar( B_value ), split_mats.get( 0 ) );
                    Core.add( split_mats.get( 1 ), new Scalar( G_value ), split_mats.get( 1 ) );
                    Core.add( split_mats.get( 2 ), new Scalar( R_value ), split_mats.get( 2 ) );
                    Core.merge( split_mats, currentFrame );
                }
            }
        }
        return currentFrame;
    }

    private static void thread_work( Mat currentFrame, int coef )
    {
        int interval = ( currentFrame.height() / processors );
        if ( processors >= 8 )
        {
            interval++;
        }
        if ( processors >= 4 )
        {
            interval++;
        }
        int end;
        for ( int i = 0; i < processors; i++ )
        {
            end = ( i + 1 ) * interval < currentFrame.height() ? ( i + 1 ) * interval : currentFrame.height();
            Deteriorationthread d_thread = new Deteriorationthread( coef, currentFrame.submat( i * interval, end, 0, currentFrame.width() ) );
            threads[i] = d_thread;
        }
        boolean done;
        for ( Deteriorationthread d_thread : threads )
        {
            d_thread.start();
        }
        do
        {
            done = true;
            for ( Deteriorationthread some_thread : threads )
            {
                done &= !some_thread.isAlive();
                if ( !done )
                {
                    break;
                }
            }
        }
        while ( !done );

    }
}

class Deteriorationthread extends Thread
{
    private final Mat currentFrame;
    public Deteriorationthread( int coef, Mat currentFrame )
    {
        super( do_deterioration( coef, currentFrame ) );
        this.currentFrame = currentFrame;
    }

    public Mat get_currentFrame()
    {
        return currentFrame;
    }

    private static Runnable do_deterioration( final int coef, final Mat currentFrame )
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                for ( int k = 0; k < currentFrame.height(); k++ )
                {
                    for ( int m = 0; m < currentFrame.width(); m++ )
                    {
                        double[] BGR = currentFrame.get( k, m );
                        if ( BGR[0] + BGR[1] + BGR[2] > 384 )
                        {
                            BGR[0] = BGR[0] + coef > 255 ? 255 : BGR[0] + coef;
                            BGR[1] = BGR[1] + coef > 255 ? 255 : BGR[1] + coef;
                            BGR[2] = BGR[2] + coef > 255 ? 255 : BGR[2] + coef;
                        }
                        else
                        {
                            BGR[0] = BGR[0] - coef < 0 ? 0 : BGR[0] - coef;
                            BGR[1] = BGR[1] - coef < 0 ? 0 : BGR[1] - coef;
                            BGR[2] = BGR[2] - coef < 0 ? 0 : BGR[2] - coef;
                        }
                        currentFrame.put( k, m, BGR );
                    }
                }
            }
        };
    }
}
