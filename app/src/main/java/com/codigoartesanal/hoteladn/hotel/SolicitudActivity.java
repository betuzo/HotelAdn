package com.codigoartesanal.hoteladn.hotel;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codigoartesanal.hoteladn.hotel.listener.OnStateSelectedListener;
import com.codigoartesanal.hoteladn.hotel.listener.OnFragmentInteractionSolicitudListener;
import com.codigoartesanal.hoteladn.hotel.model.SolicitudServicio;

public class SolicitudActivity extends ActionBarActivity
        implements OnFragmentInteractionSolicitudListener, OnStateSelectedListener {

    protected static final String TAG = SolicitudActivity.class.getSimpleName();

    public static final String TAG_SOL_LIST_FRAGMENT = "tagSolListFragment";
    public static final String TAG_SOL_DETAIL_FRAGMENT = "tagSolDetailFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,
                            SolicitudServicioFragment.newInstance(),
                            TAG_SOL_LIST_FRAGMENT)
                    .commit();
            SolicitudServicio solicitudServicio = new SolicitudServicio();
            solicitudServicio.setId(-1L);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail,
                            SolicitudServicioDetailFragment.newInstance(solicitudServicio),
                            TAG_SOL_DETAIL_FRAGMENT)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solicitud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(SolicitudServicio solicitudServicio) {
        Log.i(TAG, solicitudServicio.getServicioDesc());
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail,
                            SolicitudServicioDetailFragment.newInstance(solicitudServicio),
                            TAG_SOL_DETAIL_FRAGMENT)
                    .commit();
    }

    @Override
    public void onArticleSelected(SolicitudServicio solicitudServicio) {
        Toast.makeText(this, solicitudServicio.getEstadoSolicitud(), Toast.LENGTH_LONG).show();
        OnStateSelectedListener listener = (OnStateSelectedListener)
                getSupportFragmentManager().findFragmentByTag(SolicitudActivity.TAG_SOL_DETAIL_FRAGMENT);
        listener.onArticleSelected(solicitudServicio);
        listener = (OnStateSelectedListener)
                getSupportFragmentManager().findFragmentByTag(SolicitudActivity.TAG_SOL_LIST_FRAGMENT);
        listener.onArticleSelected(solicitudServicio);
    }
}
