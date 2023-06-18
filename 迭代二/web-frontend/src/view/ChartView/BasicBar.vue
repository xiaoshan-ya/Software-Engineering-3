<template>
  <div class="BasicBar">
<!--    <h1>{{this.head}}</h1>-->
    <div :id="head" class="chart"></div>
    <p>{{this.txt}}</p>
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
      // let resizeMainContainer = function () {
      //   chartDom.style.width = window.innerWidth * 0.7 + 'px'
      //   chartDom.style.height = window.innerHeight * 0.5 + 'px'
      // }

      // resizeMainContainer()

      let myChart = echarts.init(chartDom)
      window.onresize = () => {
        // resizeMainContainer()
        myChart.resize()
      }
      let option

      option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['positive', 'negative', 'neutral'],
          textStyle: {
            color: '#fff' // 设置图例文字颜色为白色
          }
        },
        xAxis: {
          data: this.x,
          axisLabel: {
            color: '#fff' // 设置x轴刻度标签的颜色为白色
          }
        },
        yAxis: {
          axisLine: {
            lineStyle: {
              color: '#fff' // 设置y轴线的颜色为白色
            }
          },
          axisLabel: {
            color: '#fff' // 设置y轴刻度标签的颜色为白色
          }
        },
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
.BasicBar .chart {
  width: 80%;
  top: 20px;
  height: 350px;
}
.BasicBar p, h1 {
  color: white;
}
.BasicBar {
  height: 80%;
  display: grid;
  place-items: center;
}
</style>
