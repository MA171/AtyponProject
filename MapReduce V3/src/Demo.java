import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Demo {

    public static void main(String[] args){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println( sdf.format(cal.getTime()) );
        //For testing purpose
    }

}
