package ca.robertbernier.exercices.appbobgeosilent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MySimpleArrayAdapter extends ArrayAdapter<LogEvenement> {
    private final Context context;
    private final  ArrayList<LogEvenement> values;

    public MySimpleArrayAdapter(Context context, ArrayList<LogEvenement> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_array_adapter, parent, false);

        TextView textView2 = (TextView) rowView.findViewById(R.id.secondLine);
        TextView textView1 = (TextView) rowView.findViewById(R.id.firstLine);
       TextView textView3 = (TextView) rowView.findViewById(R.id.seq_id);
        textView1.setText(values.get(position).m_dt_event);
        textView2.setText(values.get(position).m_s_even);
        if (values.get(position).m_i_evenement_ID != null)
            {
                textView3.setText(values.get(position).m_i_evenement_ID.toString());
            }
        return rowView;
    }


}