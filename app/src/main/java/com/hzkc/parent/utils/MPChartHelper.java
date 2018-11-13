package com.hzkc.parent.utils;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.hzkc.parent.R;
import com.hzkc.parent.view.MPChartMarkerView;
import com.hzkc.parent.view.MyValueFormatter;
import com.hzkc.parent.view.StringAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JKWANG-PC on 2016/10/20.
 */

public class MPChartHelper {

    public static final int[] PIE_COLORS = {
            Color.rgb(181, 194, 202), Color.rgb(129, 216, 200), Color.rgb(241, 214, 145),
            Color.rgb(108, 176, 223), Color.rgb(195, 221, 155), Color.rgb(251, 215, 191),
            Color.rgb(237, 189, 189), Color.rgb(172, 217, 243)
    };

    public static final int[] LINE_COLORS = {
            Color.rgb(140, 210, 118), Color.rgb(159, 143, 186), Color.rgb(233, 197, 23)
    };//绿色，紫色，黄色

    public static final int[] LINE_FILL_COLORS = {
            Color.rgb(222, 239, 228), Color.rgb(246, 234, 208), Color.rgb(235, 228, 248)
    };


    /**
     * 单数据集。设置柱状图样式，X轴为字符串，Y轴为数值
     *
     * @param barChart
     * @param xAxisValue
     * @param yAxisValue
     * @param title 图例文字
     * @param xAxisTextSize x轴标签字体大小
     * @param barColor
     */
    public static void setBarChart(BarChart barChart, List<String> xAxisValue, List<Float> yAxisValue, String title, float xAxisTextSize, Integer barColor) {
        barChart.getDescription().setEnabled(false);//设置描述
        barChart.setPinchZoom(true);//设置按比例放缩柱状图

        //设置自定义的markerView
        MPChartMarkerView markerView = new MPChartMarkerView(barChart.getContext(), R.layout.custom_marker_view);
        barChart.setMarker(markerView);

        //x坐标轴设置
        IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValue);//设置自定义的x轴值格式化器
        XAxis xAxis = barChart.getXAxis();//获取x轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawGridLines(false);//不绘制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextSize(xAxisTextSize);//设置标签字体大小
        xAxis.setLabelCount(xAxisValue.size());//设置标签显示的个数

        //y轴设置
        YAxis leftAxis = barChart.getAxisLeft();//获取左侧y轴
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//设置y轴标签显示在外侧
        leftAxis.setAxisMinimum(0f);//设置Y轴最小值
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);//禁止绘制y轴标签
        leftAxis.setDrawAxisLine(true);//禁止绘制y轴

        barChart.getAxisRight().setEnabled(false);//禁用右侧y轴

        //图例设置
        Legend legend = barChart.getLegend();
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);//图例水平居中
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);//图例在图表上方
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);//图例的方向为水平
        legend.setDrawInside(true);//绘制在chart的外侧
//        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//图例中的文字方向

        legend.setForm(Legend.LegendForm.EMPTY);//图例窗体的形状
        legend.setFormSize(0f);//图例窗体的大小
        legend.setTextSize(16f);//图例文字的大小
        legend.setYOffset(-2f);

        //设置柱状图数据
        setBarChartData(barChart, yAxisValue, title, barColor);
        barChart.setExtraBottomOffset(10);//距视图窗口底部的偏移，类似与paddingbottom
        barChart.setExtraTopOffset(30);//距视图窗口顶部的偏移，类似与paddingtop
        barChart.setFitBars(true);//使两侧的柱图完全显示
        barChart.animateX(1500);//数据显示动画，从左往右依次显示
    }

    /**
     * 设置柱图
     *
     * @param barChart
     * @param yAxisValue
     * @param title
     * @param barColor
     */
    private static void setBarChartData(BarChart barChart, List<Float> yAxisValue, String title, Integer barColor) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0, n = yAxisValue.size(); i < n; ++i) {
            entries.add(new BarEntry(i, yAxisValue.get(i)));
        }

        BarDataSet set1;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(entries);
            set1.setColor(ContextCompat.getColor(barChart.getContext(), R.color.blue));//设置set1的柱的颜色
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(entries, "");
            if (barColor == null) {
                set1.setColors(new int[]{Color.RED, Color.BLUE, Color.GRAY});
            } else {
                set1.setColor(barColor);
            }

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(12f);
            data.setBarWidth(0.4f);
            data.setValueFormatter(new MyValueFormatter());

            barChart.setData(data);
        }
    }
}
