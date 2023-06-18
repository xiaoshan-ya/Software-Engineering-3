<template>
  <div class="MultipleXAxes" style="display: flex;flex-direction: column; justify-content: center;align-items: center;">
    <h1>{{this.head}}</h1>
    <p>{{this.txt}}</p>
    <div :id="head" class="chart"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'Multiple-X-Axes',
  props: {
    x1: Array,
    axisName: Array,
    positive: Array,
    negative: Array,
    neutral: Array,
    txt: String,
    head: String
  },
  mounted () {
    this.showMultipleXAxesChart()
  },
  methods: {
    showMultipleXAxesChart () {
      let chartDom = document.getElementById(this.head)
      let resizeMainContainer = function () {
        chartDom.style.width = window.innerWidth * 0.7 + 'px'
        chartDom.style.height = window.innerHeight * 0.5 + 'px'
      }

      resizeMainContainer()

      let myChart = echarts.init(chartDom)
      window.onresize = () => {
        resizeMainContainer()
        myChart.resize()
      }
      let option
      console.log(this.head, 'xxxxxx ', this.x1)

      const colors = ['#5470C6', '#EE6666', '#0a8f53']
      option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: this.axisName
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.x1
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: this.axisName[0],
            textStyle: {
              color: colors[0]
            },
            type: 'line',
            data: this.positive
          },
          {
            name: this.axisName[1],
            textStyle: {
              color: colors[1]
            },
            type: 'line',
            data: this.negative
          },
          {
            name: this.axisName[2],
            textStyle: {
              color: colors[2]
            },
            type: 'line',
            data: this.neutral
          }
        ]
      }

      option && myChart.setOption(option)
    }
  }
}
</script>

<style>
.chart {
  width: 100%;
  height: 100%;

}
.MultipleXAxes {
  height: 80%;
  width: 100%;
  margin: 0;
}
p, h1 {
  color: black;
}
</style>
