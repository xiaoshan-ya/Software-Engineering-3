<template>
  <div class="bg">
    <!-- banner start -->
    <div class="caption-title">
      <i class="fl"><img src="../assets/images/title-left.png" alt=""></i>
      <span class="biaoti fl">SentiStrength</span>
      <i class="fr"><img src="../assets/images/title-right.png" alt=""></i>
    </div>
    <!-- 内容正文 -->
    <div class="zhenwen">
      <div class="left_zs fl">
        <!-- 项目概览 -->
        <div class="data-box1 left_tb fl" style="height: 383px">
          <i class="topL"></i>
          <i class="topR"></i>
          <i class="bottomL"></i>
          <i class="bottomR"></i>
          <div class="data-title">
            <b class="data-title-left fl">[</b>
            <span>项目名称</span>
            <b class="data-title-right fr">]</b>
          </div>
          <!-- 项目概览-->
          <div class="xmglan">
            <div id="xmgl_cen">
              <div class="xmgl_tb">
                <span>Apache Superset</span>
                <br><br>
                <el-select v-model="chosenCollectionID">
                  <el-option v-for="it in collectionList"
                             :label="it.name"
                             :key="it.id"
                             :value="it.id">
                  </el-option>
                </el-select>
                <br><br>
                <div class="block">
                  <span>请选择日期</span><br>
                  <el-date-picker
                    v-model="beginEndData"
                    type="daterange"
                    align="right"
                    unlink-panels
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    class="date-picker">
                  </el-date-picker>
                </div>
                <el-input v-model="username" placeholder="请输入想要查看的用户名" class="el-input"></el-input><br>
                <el-input v-model="project" placeholder="请输入想要查看的项目名" class="el-input"></el-input><br>
                <el-button @click="showProportion" type="primary" plain class="button" size="small">查看</el-button>
              </div>
              <div class="xmgl_tb none">

              </div>
            </div>
          </div>
        </div>

        <div class="data-box1 left_tb fl mtop40">
          <i class="topL"></i>
          <i class="topR"></i>
          <i class="bottomL"></i>
          <i class="bottomR"></i>
          <div class="data-title">
            <b class="data-title-left fl">[</b>
            <span>情绪占比图</span>
            <b class="data-title-right fr">]</b>
          </div>

          <div class="ryclts">
            <div id="xjfxzt" style="height:100%; width:100%">
              <MultipleXAxes
                ref="proportion-chart"
                v-bind:axisName="['positive', 'negative', 'neutral']"
                v-bind:x1="proportionX"
                v-bind:positive="proportionPositive"
                v-bind:negative="proportionNegative"
                v-bind:neutral="proportionNeutral"
                txt="折线图中两条折线，分别积极情绪和消极情绪的占比，y轴为占比比例，x1轴为评论的时间"
                head="表1">
              </MultipleXAxes>
            </div>
          </div>
        </div>

      </div>
      <div class="center_zs fl">
        <div class="data-box1 box1-back" style="height: 891px">
          <i class="topL"></i>
          <i class="topR"></i>
          <i class="bottomL"></i>
          <i class="bottomR"></i>
          <div class="data-title">
            <b class="data-title-left">[</b>
            <span>词云图</span>
            <b class="data-title-right">]</b>
          </div>

        </div>
      </div>
      <div class="right_zs fl">
        <div class="data-box1 right_tb ">
          <i class="topL"></i>
          <i class="topR"></i>
          <i class="bottomL"></i>
          <i class="bottomR"></i>
          <div class="data-title">
            <b class="data-title-left">[</b>
            <span>用户情绪值图</span>
            <b class="data-title-right">]</b>
          </div>
          <div class="ryclts">
            <div id="xjfxzt" >
              <MultipleXAxes
                ref="user-chart"
                v-bind:axisName="['positive', 'negative', 'overall']"
                v-bind:x1="userSentimentX"
                v-bind:positive="userPositiveScore"
                v-bind:negative="userNegativeScore"
                v-bind:neutral="userOverall"
                txt="输入用户名，x1轴为用户评论时间，y轴为情绪值"
                head="表2">
              </MultipleXAxes>
            </div>
          </div>
        </div>

        <div class="data-box1 right_tb mtop40">
          <i class="topL"></i>
          <i class="topR"></i>
          <i class="bottomL"></i>
          <i class="bottomR"></i>
          <div class="data-title">
            <b class="data-title-left">[</b>
            <span>情绪数量图</span>
            <b class="data-title-right">]</b>
          </div>
          <div class="ryclts">
            <div id="xjfxzt">
              <BasicBar txt="x轴为不同版本，y轴为积极情绪和消极情绪的数量"
                        head="表3"
                        ref="quantity-chart"
                        v-bind:positive="positiveQuantity"
                        v-bind:negative="negativeQuantity"
                        v-bind:neutral="neutralQuantity"
                        v-bind:x="sentimentQuantityX">
              </BasicBar>
            </div>
          </div>
        </div>
      </div>

    </div>
    <div class="h30 clearfix"></div>
  </div>
</template>

<script>
import MultipleXAxes from '@/view/ChartView/Multiple-X-Axes.vue'
import BasicBar from '@/view/ChartView/BasicBar.vue'
import {getSentiByTime, getSentiByUser, getSentiByVersion} from '@/network/main'
import {listAllCollection} from '@/network/management'

export default {
  name: 'AnalysisView',
  components: {BasicBar, MultipleXAxes},
  data () {
    return {
      collectionList: [],
      chosenCollectionID: '',
      beginEndData: '',
      username: '',
      project: '',

      proportionX: [],
      proportionPositive: [],
      proportionNegative: [],
      proportionNeutral: [],

      userSentimentX: [],
      userPositiveScore: [],
      userNegativeScore: [],
      userNeutralScore: [],

      sentimentQuantityX: [],
      positiveQuantity: [],
      negativeQuantity: [],
      neutralQuantity: []
    }
  },
  mounted () {
    listAllCollection({
      params: {}
    }).then(res => {
      if (res.code === 200) {
        this.collectionList = res.data.list
      } else {
        this.$message({
          message: res.data.list
        })
      }
    })
  },
  methods: {
    showProportion () {
      // 显示情绪占比图
      getSentiByTime({
        params: {
          collectionId: this.chosenCollectionID,
          beginDate: this.beginEndData[0],
          endDate: this.beginEndData[1]
        }
      }).then(response => {
        if (response.code === 200) {
          console.log('proportion success: ', response.data)

          let positiveList = []
          let negativeList = []
          let neutralList = []
          let proportionX = []
          for (let i = 0; i < response.data.length; i++) {
            proportionX.push(response.data[i].beginTime)

            let sum = response.data[i].negativeNumber + response.data[i].positiveNumber + response.data[i].neutralNumber

            let proportionPositive = response.data[i].positiveNumber / sum
            let proportionNegative = response.data[i].negativeNumber / sum
            let proportionNeutral = response.data[i].neutralNumber / sum

            positiveList.push(proportionPositive.toFixed(2) * 1)
            negativeList.push(proportionNegative.toFixed(2) * 1)
            neutralList.push(proportionNeutral.toFixed(2) * 1)
          }
          this.proportionX = proportionX
          this.proportionPositive = positiveList
          this.proportionNegative = negativeList
          this.proportionNeutral = neutralList

          // console.log(this.proportionX, this.proportionPositive, this.proportionNegative, this.proportionNeutral)
        } else {
          console.log('proportion fail: ', response)
        }
      }).then(() => {
        setTimeout(() => {
          this.$refs['proportion-chart'].showMultipleXAxesChart()
        }, 500)
      })

      // 显示用户情绪图
      getSentiByUser({
        params: {
          collectionId: this.chosenCollectionID,
          username: this.username
        }
      }).then(res => {
        if (res.code === 200) {
          console.log('username success: ', res.data)

          let userSentimentX = []
          let userPositiveScore = []
          let userNegativeScore = []
          for (let i = 0; i < res.data.length; i++) {
            userSentimentX.push(res.data[i].createdAt)
            userPositiveScore.push(res.data[i].positiveScore)
            userNegativeScore.push(res.data[i].negativeScore)
          }
          this.userSentimentX = userSentimentX
          this.userPositiveScore = userPositiveScore
          this.userNegativeScore = userNegativeScore

          // console.log(this.userSentimentX, this.userPositiveScore, this.userNegativeScore)
        } else {
          console.log('username fail', res)
        }
      }).then(() => {
        setTimeout(() => {
          this.$refs['user-chart'].showMultipleXAxesChart()
        }, 500)
      })

      // 显示情绪数量图
      getSentiByVersion({
        params: {
          project: this.project
        }
      }).then(res => {
        if (res.code === 200) {
          console.log('project success', res.data)

          let sentimentQuantityX = []
          let positiveQuantity = []
          let negativeQuantity = []
          let neutralQuantity = []
          for (let i = 0; i < res.data.length; i++) {
            sentimentQuantityX.push(res.data[i].version)
            positiveQuantity.push(res.data[i].positiveNumber)
            negativeQuantity.push(res.data[i].negativeNumber)
            neutralQuantity.push(res.data[i].neutralNumber)
          }
          this.sentimentQuantityX = sentimentQuantityX
          this.positiveQuantity = positiveQuantity
          this.negativeQuantity = negativeQuantity
          this.neutralQuantity = neutralQuantity

          // console.log(this.sentimentQuantityX, this.positiveQuantity, this.negativeQuantity, this.neutralQuantity)
        } else {
          console.log('project fail', res)
        }
      }).then(() => {
        setTimeout(() => {
          this.$refs['quantity-chart'].showCountChart()
        }, 500)
      })
    },
    updateChart () {
      console.log(this.proportionNeutral)
    }
  }
}
</script>

<style scoped>
@import "../assets/css/public.css";
@import "../assets/css/jianlisw.css";
@import "../assets/css/jianlixt.css";

* {
  padding:0;
  margin:0;
  font-family:"微软雅黑";
  font-size:12px;
  font-style:normal
}
dd,dl,dt,em,i,p,textarea,a,div {
  padding:0;
  margin:0;
}
a {
  text-decoration:none;
}
img {
  border:0;
}
ul,li {
  padding:0;
  margin:0;
  list-style:none;
}
.bg {
  margin:0;
  padding:0;
  color:#000;
  background:#000c3b;
}
.date-picker {
  width: 80%;
  margin-bottom: 20px;
}
.el-input {
  width: 80%;
  margin-bottom: 20px;
}
.button{
  background-color: #00a0e9;
  color: white;
  padding: 20px;
}

</style>
