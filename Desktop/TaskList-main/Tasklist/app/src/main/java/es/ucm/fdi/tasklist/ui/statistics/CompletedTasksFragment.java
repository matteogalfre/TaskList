package es.ucm.fdi.tasklist.ui.statistics;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Pair;
import androidx.fragment.app.Fragment;

import com.androidplot.Region;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.SeriesUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import es.ucm.fdi.tasklist.R;
import es.ucm.fdi.tasklist.db.DataBaseTask;

public class CompletedTasksFragment extends Fragment {
    private class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    private class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection getFormatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }


    private static final String NO_SELECTION_TXT = "";
    private static final int NUM_DISPLAYED_MONTHS = 5;
    private XYPlot plot;

    private XYSeries series1;
    private XYSeries series2;

    // Create a couple arrays of y-values to plot:

    LinkedList<Integer> monthList = new LinkedList<>();
    LinkedList<Number> totalTasksSeries = new LinkedList<>();
    LinkedList<Number> completedTasksSeries = new LinkedList<>();

    private MyBarFormatter formatter1;

    private MyBarFormatter formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_tasks, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.plot);

        formatter1 = new MyBarFormatter(Color.rgb(100, 150, 100), Color.LTGRAY);
        formatter1.setMarginLeft(PixelUtils.dpToPix(10));
        formatter1.setMarginRight(PixelUtils.dpToPix(1));
        formatter2 = new MyBarFormatter(Color.rgb(100, 100, 150), Color.LTGRAY);
        formatter2.setMarginLeft(PixelUtils.dpToPix(1));
        formatter2.setMarginRight(PixelUtils.dpToPix(10));
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT, new Size(PixelUtils.dpToPix(100), SizeMode.ABSOLUTE, PixelUtils.dpToPix(100), SizeMode.ABSOLUTE), TextOrientation.HORIZONTAL);
        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(0, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);
        selectionWidget.position(0, HorizontalPositioning.RELATIVE_TO_CENTER, PixelUtils.dpToPix(45), VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.TOP_MIDDLE);
        selectionWidget.pack();

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        plot.setLinesPerDomainLabel(1);

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });


        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new NumberFormat() {
                    @Override
                    public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                        //int year = (int) (value + 0.5d) / 12;
                        if (value == 0.0 || (int) value >= series1.size()) return new StringBuffer();
                        int month = monthList.get(((int) value)-1);
                        return new StringBuffer(DateFormatSymbols.getInstance(Locale.forLanguageTag("es-ES")).getShortMonths()[month]);
                    }

                    @Override
                    public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                        throw new UnsupportedOperationException("Not yet implemented.");
                    }

                    @Override
                    public Number parse(String string, ParsePosition position) {
                        throw new UnsupportedOperationException("Not yet implemented.");
                    }
                });

        loadData();

        updatePlot();
    }

    private void loadData(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;

        for(int i = 0; i < NUM_DISPLAYED_MONTHS; ++i){
            totalTasksSeries.addFirst(getTotalTasks(month));
            completedTasksSeries.addFirst(getCompletedTasks(month));
            monthList.addFirst(month-1);

            month = (month - 1) % 12;
        }

        totalTasksSeries.addFirst(null);
        completedTasksSeries.addFirst(null);

    }

    private int getTotalTasks(int month){
        int count = 0;
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String strMonth = (month < 10) ? "0" + month : String.valueOf(month);
        Cursor c = db.rawQuery("SELECT COUNT(date) FROM tasks WHERE strftime('%m', date) = '" + strMonth + "'", null);
        if (c.moveToFirst()) {
            count = c.getInt(0);
            //String date = c.getString(1);
            //Log.d("F", String.valueOf(count));
        }
        c.close();
        return count;
    }

    private int getCompletedTasks(int month){
        int count = 0;
        DataBaseTask dbHelper = DataBaseTask.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String strMonth = (month < 10) ? "0" + month : String.valueOf(month);
        Cursor c = db.rawQuery("SELECT COUNT(date) FROM tasks WHERE fin = '1' AND strftime('%m', date) = '" + strMonth + "'", null);
        if (c.moveToFirst()) {
            count = c.getInt(0);
            //String date = c.getString(1);
            //Log.d("F", String.valueOf(count));
        }
        c.close();
        return count;
    }

    private void updatePlot() {

        // Remove all current series from each plot
        plot.clear();

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(totalTasksSeries, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Tareas totales");
        series2 = new SimpleXYSeries(completedTasksSeries, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Tareas completadas");

        //plot.setDomainBoundaries(series1.size(), BoundaryMode.FIXED);
        plot.getInnerLimits().setMaxX(series1.size());
        plot.getInnerLimits().setMinX(0);
        plot.setRangeUpperBoundary(SeriesUtils.minMax(series1, series2).getMaxY().intValue() + 1, BoundaryMode.FIXED);

        plot.setDomainStep(StepMode.SUBDIVIDE, series1.size()+1);


        plot.addSeries(series1, formatter1);
        plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
        renderer.setBarOrientation((BarRenderer.BarOrientation) BarRenderer.BarOrientation.SIDE_BY_SIDE);

        final BarRenderer.BarGroupWidthMode barGroupWidthMode = (BarRenderer.BarGroupWidthMode) BarRenderer.BarGroupWidthMode.FIXED_GAP;
        renderer.setBarGroupWidth(barGroupWidthMode, 1);

        plot.getInnerLimits().setMaxY(0);

        plot.getLegend().setSize(new Size(PixelUtils.dpToPix(25), SizeMode.ABSOLUTE, 1.0f, SizeMode.RELATIVE));
        plot.getLegend().position(PixelUtils.dpToPix(30), HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_BOTTOM);
        plot.getLegend().setAnchor(Anchor.LEFT_BOTTOM);
        //Paint legendPaint = new Paint();
        //legendPaint.setARGB(255, 100, 100, 150);
        //plot.getLegend().setBackgroundPaint(legendPaint);

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well.
        if (plot.containsPoint(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);

            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (SeriesBundle<XYSeries, ? extends XYSeriesFormatter> sfPair : plot
                    .getRegistry().getSeriesAndFormatterList()) {
                XYSeries series = sfPair.getSeries();
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                Region.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                Region.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if (selection == null) {
            Paint p = new Paint();
            p.setARGB(0, 0, 0, 0);
            selectionWidget.setBackgroundPaint(p);
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            // add a dark, semi-transparent background to the selection label widget:
            Paint p = new Paint();
            p.setARGB(100, 0, 0, 0);
            int month = (int) selection.second.getX(selection.first) - 1;
            String strMonth = DateFormatSymbols.getInstance(Locale.forLanguageTag("es-ES")).getShortMonths()[month];
            selectionWidget.setBackgroundPaint(p);
            selectionWidget.setText(selection.second.getTitle() +
                    ": " + selection.second.getY(selection.first) + " en " + strMonth);
        }
        plot.redraw();
    }
}
