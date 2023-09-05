package com.example.application.views;

import com.example.application.data.entity.Patience;
import com.example.application.data.entity.Doctor;
import com.example.application.data.presenter.DoctorPresenter;
import com.example.application.data.service.DoctorStatisticsService;
import com.example.application.data.statistics.DoctorStatistics;
import com.example.application.util.ResourceBundleUtil;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
@Route(value = "personnel-dashboard", layout = MainLayout.class)
@PageTitle("Emre HBYS")
@PermitAll
public class DoctorDashboardView extends VerticalLayout {


    String lang;
    ResourceBundleUtil rb;
    DoctorPresenter personnelPresenter;
    DoctorStatisticsService personnelStatisticsService;
    Chart genderPieChart;

    List<Doctor> personnelList;
    Grid<Doctor> grid;


    public DoctorDashboardView(DoctorPresenter personnelPresenter, DoctorStatisticsService personnelStatisticsService) {
        lang = VaadinSession.getCurrent().getAttribute("language").toString();
        rb = new ResourceBundleUtil(lang);

        this.personnelPresenter = personnelPresenter;
        this.personnelStatisticsService = personnelStatisticsService;
        this.personnelList = personnelPresenter.findAllPersonnel("");
        addClassName("list-view");
        setSizeFull();
        grid = new Grid<>(Doctor.class);
        genderPieChart = new Chart(ChartType.PIE);

        configureGrid();
        add(grid,getChartLayout());
    }


    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("personnelId", "name", "lastName");

        grid.addColumn(personnel -> DoctorPresenter.formatPhoneNumber(personnel.getPhone())).setAutoWidth(true).setKey("phone");


        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> updatePieChart(event.getValue()));

        grid.getColumnByKey("personnelId").setHeader(rb.getString("personnelId"));
        grid.getColumnByKey("name").setHeader(rb.getString("name"));
        grid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        grid.getColumnByKey("phone").setHeader(rb.getString("phone"));

        grid.setItems(personnelList);
    }

    private Chart updatePieChart(Doctor personnel){

        if(personnel != null) {
            DoctorStatistics personnelStatistics = personnelStatisticsService.getPersonnelStatistics(personnel.getPersonnelId().toString());

            long maleCount = personnelStatistics.getMaleGenderPatienceCount();
            long femaleCount = personnelStatistics.getFemaleGenderPatienceCount();
            long otherCount = personnelStatistics.getOtherGenderPatienceCount();

            System.out.println(maleCount + " "+ femaleCount + " " +otherCount );

            //genderPieChart = new Chart(ChartType.PIE);
            Configuration conf = genderPieChart.getConfiguration();
            String title = rb.getString("pieChartTitle");
            conf.setTitle(MessageFormat.format(title, personnel.getName() + " " + personnel.getLastName()));

            Tooltip tooltip = new Tooltip();
            tooltip.setValueDecimals(1);
            conf.setTooltip(tooltip);

            PlotOptionsPie plotOptions = new PlotOptionsPie();
            plotOptions.setAllowPointSelect(true);
            plotOptions.setCursor(Cursor.POINTER);
            plotOptions.setShowInLegend(true);
            conf.setPlotOptions(plotOptions);

            DataSeries series = new DataSeries(rb.getString("patienceCount"));

            DataSeriesItem male = new DataSeriesItem(rb.getString("Erkek"), maleCount);
            male.setSliced(true);
            male.setSelected(true);

            DataSeriesItem female = new DataSeriesItem(rb.getString("Kadın"), femaleCount);
            female.setColorIndex(5);
            DataSeriesItem other = new DataSeriesItem(rb.getString("Diğer"), otherCount);

            series.add(male);
            series.add(female);
            series.add(other);

            conf.setSeries(series);
            genderPieChart.setVisibilityTogglingDisabled(true);
            genderPieChart.drawChart();

        }
        return genderPieChart;
    }

    private FormLayout getChartLayout() {
        FormLayout chartLayout = new FormLayout();
        chartLayout.setSizeFull();
        chartLayout.setClassName("chart-layout");
        //*****************TEST VALUE = 1
        //chartLayout.add(getGenderPieChart(personnelPresenter.findById("1")));
        chartLayout.add(updatePieChart(personnelList.get(0)), getPatiencesPerPersonnelBarChart());
        return chartLayout;

    }


    private Chart getPatiencesPerPersonnelBarChart(){


        List<DoctorStatistics> statList = personnelStatisticsService.getAllPersonnelStatistics();



        Chart barChart = new Chart(ChartType.COLUMN);

        Configuration conf = barChart.getConfiguration();
        conf.setTitle(rb.getString("barChartTitle"));

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        ListSeries series = new ListSeries(rb.getString("patienceCount"));



        for(DoctorStatistics ps : statList) {
            x.addCategory(ps.getFullName());

            series.addData(ps.getPatienceCount());


        }

        conf.addSeries(series);

        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle(rb.getString("patienceCount"));
        conf.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        conf.setTooltip(tooltip);

        return  barChart;

    }

}
