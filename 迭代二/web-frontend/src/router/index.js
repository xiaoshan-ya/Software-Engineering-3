import Vue from 'vue'
import Router from 'vue-router'
import MainView from '@/view/MainView.vue'
import ManagementView from '@/view/ManagementView'
import AnalysisView from '@/view/AnalysisView'
import ChartView from '@/view/ChartView'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/main',
      name: 'MainView',
      component: MainView
    },
    {
      path: '/management',
      name: 'ManagementView',
      component: ManagementView
    },
    {
      path: '/',
      name: 'AnalysisView',
      component: AnalysisView
    },
    {
      path: '/chart',
      name: 'ChartView',
      component: ChartView
    }
  ]
})
