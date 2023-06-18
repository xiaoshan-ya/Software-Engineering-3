<template>
<div>
  <NavMenu></NavMenu>
<el-container >
  <el-header>
    <div style="text-align: center;font-size: 2rem;">
        数据管理
    </div>
  </el-header>
  <el-container style="height: 100%;margin-left: 5%;">
    <el-aside class="zhenwen" style="width: 15%;">
      <div class="center_zs fl" style="width: 90%;">
        <div>
          <div class="data-title" style="background-color: transparent">
            <b class="data-title-left">[</b>
            <span>数据集
                <el-button :circle="true" plain="plain" @click="formVisible = true">
                <i class="el-icon-plus"></i>
              </el-button>
            </span>
            <b class="data-title-right">]</b>
            <span>获取数据
            <el-button :circle="true" plain="plain" @click="spiderVisible = true">
              <i class="el-icon-more"></i>
            </el-button>
            </span>
          </div>
          <div>
            <el-collapse>
              <el-collapse-item name="-1">
                <template #title>
                  <div style="display: flex;flex-direction: row; flex: 1;align-items: center; justify-content: space-between; ">
                    <div>
                      Add Data
                    </div>
                    <el-button type="primary" size="mini" plain="plain" :circle="true" @click="getData(-1);currentCollection=-1"><i class="el-icon-s-operation" ></i></el-button>
                  </div>
                </template>
                <div>
                  All Data
                </div>
              </el-collapse-item>
                <el-collapse-item v-for="(item, index) in collectionList" :index="index" :key="item.id" :name="index">
                  <template #title>
                    <div style="display: flex;flex-direction: row; flex: 1;align-items: center; justify-content: space-between; ">
                      <div>
                        {{item.name}}
                      </div>
                      <div>
                        <el-popconfirm
                          title="Are you sure to delete this?"
                          @confirm="deleteCollection(item.id)"
                        >
                          <template #reference>
                            <el-button type="danger" size="mini" plain="plain" :circle="true"><i class="el-icon-close"></i></el-button>
                          </template>
                        </el-popconfirm>
                        <el-button type="primary" size="mini" plain="plain" :circle="true" @click="getData(item.id);currentCollection=item.id"><i class="el-icon-s-operation" ></i></el-button>
                      </div>
                      </div>
                  </template>
                  <div>
                    {{item.description}}
                  </div>
                </el-collapse-item>
              </el-collapse>
          </div>
        </div>
      </div>
    </el-aside>
    <el-main style="overflow: clip">
      <div class="data-title" style="margin-top:30px; background-color: transparent">
        <b class="data-title-left">[</b>
        <span>数据展示</span>
        <b class="data-title-right">]</b>
      </div>
      <div class="top" style="text-align: left;">
        <div style="margin: 20px">
          <el-input
            v-model="search"
            style="width: 50%"
            placeholder="search"/>
          查询条数(页大小:)
          <el-input
            v-model="pageSize"
            style="width: 100px"
            placeholder="请输入查询条数"/>
        </div>
        <div>
          添加至数据集:
          <el-select v-model="chosenCollection">
            <el-option v-for="it in collectionList"
                       :label="it.name"
                       :key="it.id"
                       :value="it.id"
            >
            </el-option>
          </el-select>
          <el-button plain="plain" @click="addDataToCollection" style="width: 70px;height: 40px;">添加</el-button>
        </div>
      </div>
      <div style="text-align: center;">
        <el-table
        :data="dataList.filter(data => !search || data.labels.includes(search) || data.content.includes(search))"
        style="width: 100%;"
        :header-cell-style="{'text-align': 'center'}"
        :cell-style="{'text-align': 'center'}"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column sortable
          prop="issueNumber"
          label="issue no"
          width="100">
        </el-table-column>
        <el-table-column
          prop="internalIssueNumber"
          label="internal no"
          width="100">
        </el-table-column>
        <el-table-column sortable
          prop="username"
          label="username"
          width="120">
        </el-table-column>
        <el-table-column sortable
          prop="projectName"
          label="project"
          width="100">
        </el-table-column>
        <el-table-column sortable
          prop="versionNumber"
          label="version"
          width="100">
        </el-table-column>
        <el-table-column
          type="expand"
          label="details"
          width="100">
          <template #default="props">
            <div>content: {{props.row.content}}</div>
            <div>labels: {{props.row.labels}}</div>
            <div>isPullRequest: {{props.row.isPullRequest}}</div>
          </template>
        </el-table-column>
        <el-table-column sortable
          prop="createdAt"
          label="created time"
          width="150">
        </el-table-column>
        <el-table-column sortable
          prop="endedAt"
          label="ended time"
          width="150">
        </el-table-column>
        <el-table-column sortable
          prop="positiveScore"
          label="p_score"
          width="100">
        </el-table-column>
        <el-table-column sortable
          prop="negativeScore"
          label="n_score"
          width="100">
        </el-table-column>
      </el-table>
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            layout="prev, pager, next"
            :total="2000"
            @current-change="pageChange"
          />
      </div>
    </el-main>
  </el-container>

</el-container>

  <el-dialog title="创建数据集"
  :visible.sync="formVisible"
  width="40%"
  :before-close="handleClose"
  >
    <div style="width: 90%; margin: auto">
      <el-form :model="collectionForm" :rules="rules" ref="collectionForm">
        <el-form-item label="数据集名称：" prop="name">
          <el-input v-model="collectionForm.name"></el-input>
        </el-form-item>
        <el-form-item label="数据集描述：" prop="description">
          <el-input v-model="collectionForm.description"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button size="medium" style="width: 70px;height:30px; font-size: 0.6rem " plain="plain" @click="createCollection('collectionForm')">创建</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-dialog>

  <el-dialog title="获取数据"
             :visible.sync="spiderVisible"
             width="40%"
             :before-close="handleClose"
  >
    <div style="width: 90%; margin: auto">
      <el-form :model="spiderForm" :rules="spiderRules" ref="spiderForm">
        <el-form-item label="项目名称：" prop="project">
          <el-input v-model="spiderForm.project"></el-input>
        </el-form-item>
        <el-form-item label="版本号：" prop="version">
          <el-input v-model="spiderForm.version"></el-input>
        </el-form-item>
        <el-form-item label="项目地址：" prop="web-address">
          <el-input v-model="spiderForm['web-address']"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :loading="isSpiderBusy" size="medium" style="width: 70px;height:30px; font-size: 0.6rem " plain="plain" @click="spider('spiderForm')">创建</el-button>
        </el-form-item>
      </el-form>
    </div>

  </el-dialog>
</div>
</template>

<script>
import {
  addCollection,
  addCollectionData, deleteCollection,
  listAllCollection,
  listAllData,
  listCollectionData
} from '@/network/management'
import axios from 'axios'
import NavMenu from '@/components/NavMenu'

export default {
  name: 'ManagementView',
  components: {NavMenu},
  mounted () {
    listAllData({
      params: {
        page: this.currentPage - 1,
        size: 100
      }
    }).then(res => {
      if (res.code === 200) {
        this.dataList = res.data.list
      } else {
        this.$message({
          message: res.data.list
        })
      }
    })
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
  data () {
    return {
      pageSize: 50,
      currentPage: 0,
      currentCollection: -1,
      formVisible: false,
      spiderVisible: false,
      isSpiderBusy: false,
      collectionForm: {},
      spiderForm: {},
      chosenCollection: '',
      chosenData: [],
      collectionList: [],
      dataList: [],
      search: '',
      classificationList: [],
      rules: {
        name: [
          {required: true, message: '请输入名称', trigger: 'change'}
        ]
      },
      spiderRules: {
        project: [
          {required: true, message: '请输入项目名称', trigger: 'blur'}
        ],
        version: [
          {required: true, message: '请输入版本号', trigger: 'blur'}
        ],
        'web-address': [
          {required: true, message: '请输入地址', trigger: 'blur'}
        ],
      }
    }
  },
  methods: {
    pageChange (val) {
      this.currentPage = val
      console.log(this.currentPage)
      document.documentElement.scrollTop = 0
      document.body.scrollTop = 0
      this.getData(this.currentCollection)
    },
    addDataToCollection () {
      if (this.chosenData.length !== 0 && this.chosenCollection !== '') {
        addCollectionData(this.chosenData, {
          params: {
            collectionId: this.chosenCollection,
          }
        }).then(res => {
          if (res.code === 200) {
            this.$message.success('添加成功！')
          } else {
            this.$message.error(res.message)
          }
        })
      } else {
        this.$message.warning('请选择数据!')
      }
    },
    handleClose (done) {
      this.$confirm('确认关闭?')
        .then(_ => {
          this.collectionForm = {}
          done()
        })
        .catch(_ => {})
    },
    spider (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.isSpiderBusy = true
          let fd = new FormData()
          fd.append('project', this.spiderForm.project)
          fd.append('version', this.spiderForm.version)
          fd.append('web-address', this.spiderForm['web-address'])
          axios.post('http://124.70.198.102:5000/process', fd, {
            headers: {
              'Content-Type': 'multipart/form-data',
              'Access-Control-Allow-Origin': '*'
            }
          }).then(res => {
            console.log(res.result)
            if (res.status === 'success') {
              this.$message.success('创建成功！')
              this.formVisible = false
              this.collectionForm = {}
              this.refresh()
            } else {
              this.$message({
                message: res.detail,
                type: 'error'
              })
            }
            this.isSpiderBusy = false
          })
        }
      })
    },
    createCollection (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          addCollection(this.collectionForm).then(res => {
            console.log(res.result)
            if (res.code === 200) {
              this.$message.success('创建成功！')
              this.formVisible = false
              this.collectionForm = {}
              this.refresh()
            } else {
              this.$message({
                message: res.message
              })
            }
          })
        }
      })
    },
    refresh () {
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
    handleSelectionChange (val) {
      this.chosenData = val.map(it => {
        return it.id
      })
      console.log(this.chosenData)
    },
    deleteCollection (id) {
      deleteCollection({
        params: {
          collectionId: id
        }
      }).then(res => {
        if (res.code === 200) {
        this.$message.success("删除成功")
          this.refresh()
      } else {
        this.$message.error("删除失败")
      }
    })
    },
    getData (id) {
      console.log(id)
      if (id === -1) {
        listAllData({
          params: {
            page: this.currentPage,
            size: this.pageSize
          }
        }).then(res => {
          if (res.code === 200) {
            this.dataList = res.data.list
          } else {
            this.$message({
              message: res.data.list
            })
          }
        })
      } else {
        listCollectionData({
          params: {
            collectionId: id,
            page: this.currentPage,
            size: this.pageSize
          }
        }).then(res => {
          if (res.code === 200) {
            if (this.currentPage > res.data.pages) {
              this.$message.warning("没有更多数据!")
              this.dataList = []
            } else {
              console.log(res.data)
              this.dataList = res.data.list
            }
          } else {
            this.$message({
              message: res.message
            })
          }
        })
      }
    }
  }
}
</script>

<style scoped>
@import "../assets/css/public.css";
@import "../assets/css/jianlisw.css";
@import "../assets/css/jianlixt.css";
.el-collapse-item .el-collapse-item__header{
  border-bottom: 0;
  border-top: 0;
  background-color: rgba(255,255,255,0.1);
}

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
</style>
