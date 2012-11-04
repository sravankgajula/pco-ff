package net.udsholt.peytz.firmafon.ui;

import net.udsholt.peytz.firmafon.R;
import net.udsholt.peytz.firmafon.domain.Reception;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReceptionAdapter extends ArrayAdapter<Reception>
{
	private static LayoutInflater inflater = null;
	
	public ReceptionAdapter(Context context, int textViewResourceId) 
	{
		super(context, textViewResourceId);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View v = convertView;
		if (v == null) {
			v = inflater.inflate(R.layout.row_reception, null);
		}

		Reception reception = this.getItem(position);

		if (reception != null) 
		{
			TextView tt = (TextView) v.findViewById(R.id.row_reception_toptext);
			TextView bt = (TextView) v.findViewById(R.id.row_reception_bottomtext);
			View at     =            v.findViewById(R.id.row_reception_active);
			
			if (at != null) {
				at.setVisibility(reception.isCloak ? View.VISIBLE : View.INVISIBLE);
			}
			
			if (tt != null) {
				tt.setText(reception.name);
			}
			
			if (bt != null) {
				bt.setText(reception.number);
			}
		}

		return v;
	}
}
