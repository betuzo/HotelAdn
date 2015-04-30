package com.codigoartesanal.hoteladn.hotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codigoartesanal.hoteladn.hotel.R;
import com.codigoartesanal.hoteladn.hotel.model.SolicitudServicio;

import java.util.List;

/**
 * Created by betuzo on 29/04/15.
 */
public class SolicitudServicioArrayAdapter extends ArrayAdapter<SolicitudServicio> {
    private final LayoutInflater mInflater;

    public SolicitudServicioArrayAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<SolicitudServicio> data) {
        clear();
        if (data != null) {
            for (SolicitudServicio appEntry : data) {
                add(appEntry);
            }
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.solicitud_row, parent, false);
        } else {
            view = convertView;
        }

        SolicitudServicio item = getItem(position);
        ((TextView)view.findViewById(R.id.service_desc)).setText(item.getServicioDesc());
        ((TextView)view.findViewById(R.id.solicitud_state)).setText(item.getEstadoSolicitud());
        ((TextView)view.findViewById(R.id.solicitud_date)).setText(item.getFechaSolicitud().toString());

        return view;
    }
}
