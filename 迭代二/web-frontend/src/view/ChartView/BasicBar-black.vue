<template>
  <div class="BasicBar" style="display: flex;flex-direction: column; justify-content: center;align-items: center;">
    <h1>{{this.head}}</h1>
    <p>{{this.txt}}</p>
    <div :id="head" class="chart"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'BasicBar',
  props: {
    x: Array,
    positive: Array,
    negative: Array,
    neutral: Array,
    txt: String,
    head: String
  },
  mounted () {
    this.showCountChart()
  },
  methods: {
    showCountChart () {
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
      console.log(this.head, ': ', this.x)

      option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['positive', 'negative', 'neutral']
        },
        xAxis: {
          data: this.x
        },
        yAxis: {},
        series: [{
          name: 'positive',
          type: 'bar',
          data: this.positive
        }, {
          name: 'negative',
          type: 'bar',
          data: this.negative
        }, {
          name: 'neutral',
          type: 'bar',
          data: this.neutral
        }]
      }

      option && myChart.setOption(option)
    }
  }
}
</script>

<style>
.chart {
  width: 80%;
  height:400px;
}
p, h1 {
  color: black;
}
.BasicBar {
  text-align: center;
  width: 100%;
  /*border-radius: 4px;*/
  /*border: 1px solid #EBEEF5;*/
  /*box-shadow: 2px 2px 5px #ccc; !* 添加2像素偏移的5像素模糊灰色阴影 *!*/
  /*background-color: #FFF;*/
  /*overflow: hidden;*/
  /*color: #303133;*/
  //-webkit-transition: .3s;
  //transition: .3s;
}
</style>
