package ca.robertbernier.exercices.appbobgeosilent;


/**
 * Created by bob on 12/7/2017.
 */

public class LogEvenement {
    public final static String TABLE = "LogEvenement";
    public final static String KEY_ID = "id";
    public final static String KEY_date_evenement = "date_evenement";
    public final static String KEY_event  = "event";

    public Integer  m_i_evenement_ID;
    public String m_dt_event;
    public String   m_s_even;

    LogEvenement(String p_dt_event , String p_s_even )
    {
        m_dt_event = p_dt_event;
        m_s_even = p_s_even;
    }

    LogEvenement(Integer p_id, String p_dt_event , String p_s_even )
    {
        m_dt_event = p_dt_event;
        m_s_even = p_s_even;
        m_i_evenement_ID = p_id;
    }

//    @Override
//  /*Comparator for sorting the list by Student Name*/
//
//    public static Comparator<LogEvenement> StuNameComparator = new Comparator<LogEvenement>() {
//
//
//
//        public int compare(LogEvenement s1, LogEvenement s2) {
//
//            String StudentName1 = s1.getStudentname().toUpperCase();
//
//            String StudentName2 = s2.getStudentname().toUpperCase();
//
//
//
//            //ascending order
//
//            return StudentName1.compareTo(StudentName2);
//
//
//
//            //descending order
//
//            //return StudentName2.compareTo(StudentName1);
//
//        }};
}
