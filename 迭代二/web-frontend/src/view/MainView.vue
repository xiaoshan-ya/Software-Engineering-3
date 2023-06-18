<template>
  <div>
    <NavMenu></NavMenu>
    <h1>SentiStrength</h1>
    <el-checkbox-group
      v-model="optionsList"
      :min="0"
      :max="200">
      <el-checkbox-button class="checkboxParam" v-for="param in params" :label="param" :key="param">{{param}}</el-checkbox-button>
    </el-checkbox-group>
    <el-input class="inputParam" v-model="wordsBeforeKeywords" placeholder="wordsBeforeKeywords"></el-input>
    <el-input class="inputParam" v-model="wordsAfterKeywords" placeholder="wordsAfterKeywords"></el-input>
    <el-input class="inputParam" v-model="negativeMultiplier" placeholder="negativeMultiplier"></el-input>
    <el-input class="inputParam" v-model="minPunctuationWithExclamation" placeholder="minPunctuationWithExclamation"></el-input>
    <el-input class="inputParam" v-model="mood" placeholder="mood"></el-input>
    <el-input class="inputParam" v-model="illegalDoubleLettersInWordMiddle" placeholder="illegalDoubleLettersInWordMiddle"></el-input>
    <el-input class="inputParam" v-model="illegalDoubleLettersAtWordEnd" placeholder="illegalDoubleLettersAtWordEnd"></el-input>
    <el-input class="inputParam" v-model="negatedWordStrengthMultiplier" placeholder="negatedWordStrengthMultiplier"></el-input>
    <el-input class="inputParam" v-model="maxWordsBeforeSentimentToNegate" placeholder="maxWordsBeforeSentimentToNegate"></el-input>
    <el-input class="inputParam" v-model="maxWordsAfterSentimentToNegate" placeholder="maxWordsAfterSentimentToNegate"></el-input>
    <el-input class="inputParam" v-model="MinSentencePosForQuotesIrony" placeholder="MinSentencePosForQuotesIrony"></el-input>
    <el-input class="inputParam" v-model="MinSentencePosForPunctuationIrony" placeholder="MinSentencePosForPunctuationIrony"></el-input>
    <el-input class="inputParam" v-model="MinSentencePosForTermsIrony" placeholder="MinSentencePosForTermsIrony"></el-input>
    <el-input class="inputParam" v-model="MinSentencePosForAllIrony" placeholder="MinSentencePosForAllIrony"></el-input>
    <el-input class="inputParam" v-model="keyword" placeholder="请输入关键词"></el-input>

    <el-select class="inputParam" v-model="select" placeholder="请选择分析模式">
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.value"
        :value="item.value">
      </el-option>
    </el-select>
    <br>
    <div>
      <el-radio-group v-model="param1">
        <el-radio @click.native.prevent="changeRadio('sentiment', 1)" label="sentiment" border>sentiment</el-radio>
        <el-radio @click.native.prevent="changeRadio('stress', 1)" label="stress" border>stress</el-radio>
      </el-radio-group>
    </div>
    <br>
    <div>
      <el-radio-group v-model="param2">
        <el-radio @click.native.prevent="changeRadio('sentenceCombineAv', 2)" label="sentenceCombineAv" border>sentenceCombineAv</el-radio>
        <el-radio @click.native.prevent="changeRadio('sentenceCombineTot', 2)" label="sentenceCombineTot" border>sentenceCombineTot</el-radio>
      </el-radio-group>
    </div>
    <br>
    <div>
      <el-radio-group v-model="param3">
        <el-radio @click.native.prevent="changeRadio('paragraphCombineAv', 3)" label="paragraphCombineAv" border>paragraphCombineAv</el-radio>
        <el-radio @click.native.prevent="changeRadio('paragraphCombineTot', 3)" label="paragraphCombineTot" border>paragraphCombineTot</el-radio>
      </el-radio-group>
    </div>
    <br>

    <el-button @click="setCorpus" type="primary" plain style="margin-bottom: 30px">创建语料库</el-button>
    <el-row class="el-row">
      <el-col :span="8" class="el-col">
        <div class="grid-content bg-purple">
          请输入文本
        </div>
      </el-col>
      <el-col :span="8" class="el-col">
        <div class="grid-content bg-purple-light">
          <el-input class="text-item" placeholder="请输入待检测文本" v-model="text"></el-input>
          <br>
          <el-button @click="getTextScore" type="primary" plain>开始检测</el-button>
        </div>
      </el-col>
      <el-col :span="8" class="el-col">
        <div class="grid-content bg-purple">
          {{ text_score }}
        </div>
      </el-col>
    </el-row>

    <el-row class="el-row">
      <el-col :span="8" class="el-col">
        <div class="grid-content bg-purple">
          请上传文件
        </div>
      </el-col>
      <el-col :span="8" class="el-col">
        <div class="grid-content bg-purple-light">
          <el-upload
            class="file-upload"
            action="action"
            :on-preview="handlePreview"
            :before-upload="beforeUpload"
            :http-request="fileUpload"
            :on-success="getFileScore"
            multiple
            :limit="1"
            :file="file">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传一个txt文件，且不超过20MB</div>
          </el-upload>
          <el-button @click="getFileScore" type="primary" plain>开始检测</el-button>
        </div>
      </el-col>
      <el-col :span="8" class="el-col">
        <div class="grid-content bg-purple">
          {{ file_score }}
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {analyzeText} from '@/network/main'
import axios from 'axios'
import NavMenu from '@/components/NavMenu'
const optionsParam = ['noBoosters', 'noNegatingPositiveFlipsEmotion', 'noNegatingNegativeNeutralisesEmotion', 'noNegators', 'noIdioms',
  'questionsReduceNeg', 'noEmoticons', 'exclamations2', 'noMultiplePosWords', 'noMultipleNegWords', 'noIgnoreBoosterWordsAfterNegatives',
  'noDictionary', 'noDeleteExtraDuplicateLetters', 'noMultipleLetters', 'negatingWordsDontOccurBeforeSentiment', 'negatingWordsOccurAfterSentiment',
  'alwaysSplitWordsAtApostrophes', 'capitalsBoostTermSentiment', 'explain', 'echo', 'UTF8']

export default {
  name: 'MainView',
  components: {NavMenu},
  data () {
    return {
      initCorpus: false,
      text_score: '',
      file_score: '',
      text: '',
      file: '',
      select: '',
      keyword: '',
      options: [{
        value: 'trinary'
      }, {
        value: 'binary'
      }, {
        value: 'scale'
      }],
      action: 'http://124.70.198.102:3456',
      mode: {},
      params: optionsParam,
      optionsList: [],
      wordsBeforeKeywords: '',
      wordsAfterKeywords: '',
      negativeMultiplier: '',
      minPunctuationWithExclamation: '',
      mood: '',
      illegalDoubleLettersInWordMiddle: '',
      illegalDoubleLettersAtWordEnd: '',
      negatedWordStrengthMultiplier: '',
      maxWordsBeforeSentimentToNegate: '',
      maxWordsAfterSentimentToNegate: '',
      MinSentencePosForQuotesIrony: '',
      MinSentencePosForPunctuationIrony: '',
      MinSentencePosForTermsIrony: '',
      MinSentencePosForAllIrony: '',
      param1: '',
      param2: '',
      param3: ''
    }
  },
  mounted () {
  },

  methods: {
    getTextScore () {
      if (this.select === '') {
        this.$message.error('请务必选择一个分析模式')
        return
      } else if (this.text === '') {
        this.$message.error('请务必输入待检测文本')
        return
      } else if (this.initCorpus === false) {
        this.$message.error('请务必初始化语料库')
        return
      }
      analyzeText(this.text).then(res => {
        if (res.code === 200) {
          this.text_score = res.data
          this.$message({
            message: '恭喜，分析成功',
            type: 'success'
          })
        } else {
          console.log(res.message)
        }
      })
    },

    getFileScore () {
      console.log(this.file)
      if (this.file === '') {
        this.$message.error('请务必上传文件')
        return
      } else if (this.initCorpus === false) {
        this.$message.error('请务必初始化语料库')
        return
      }
      let fd = new FormData()
      fd.append('file', this.file)
      axios.post('http://124.70.198.102:3456/text/file', fd, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        if (response.status === 200) {
          console.log('res:', response)
          this.file_score = 'success'
          // 创建一个Blob对象
          const blob = new Blob([response.data], {type: response.headers['content-type']})
          // 创建一个URL对象
          const url = window.URL.createObjectURL(blob)
          // 创建一个a标签
          const link = document.createElement('a')
          link.href = url
          link.download = 'SentiStrength_Result.txt'
          // 模拟点击a标签
          link.click()
          this.$message({
            message: '恭喜，分析成功',
            type: 'success'
          })
        } else {
          console.log('fail')
        }
      }).catch(error => {
        console.log('error', error)
      })
    },

    fileUpload (item) {
      this.file = item.file
      this.mode = item.file
      this.$message({
        message: '恭喜，上传成功',
        type: 'success'
      })
    },

    handlePreview (file) {
    },

    setCorpus () {
      let options = []
      options = this.getOptions(options)
      if (this.select !== '') {
        options.push(this.select)
      } else {
        this.$message.error('请务必选择一个分析模式')
        return
      }
      console.log(options)
      axios.post('http://124.70.198.102:3456/text/corpus?' + options.map(s => {
        return 'options=' + s
      }).join('&')).then(res => {
        console.log(res)
        if (res.status === 200) {
          this.initCorpus = true
          this.$message({
            message: '恭喜，创建成功',
            type: 'success'
          })
        }
      })
    },

    getOptions (options) {
      options = options.concat(this.optionsList)

      if (this.param1 !== '') {
        options.push(this.param1)
      }
      if (this.param2 !== '') {
        options.push(this.param2)
      }
      if (this.param3 !== '') {
        options.push(this.param3)
      }

      if (this.keyword !== '') {
        options.push(this.keyword)
      }

      if (this.wordsBeforeKeywords !== '') {
        options.push('wordsBeforeKeywords', this.wordsBeforeKeywords)
      }
      if (this.wordsAfterKeywords !== '') {
        options.push('wordsAfterKeywords', this.wordsAfterKeywords)
      }
      if (this.negativeMultiplier !== '') {
        options.push('negativeMultiplier', this.negativeMultiplier)
      }
      if (this.minPunctuationWithExclamation !== '') {
        options.push('minPunctuationWithExclamation', this.minPunctuationWithExclamation)
      }
      if (this.mood !== '') {
        options.push('mood', this.mood)
      }
      if (this.illegalDoubleLettersInWordMiddle !== '') {
        options.push('illegalDoubleLettersInWordMiddle', this.illegalDoubleLettersInWordMiddle)
      }
      if (this.illegalDoubleLettersAtWordEnd !== '') {
        options.push('illegalDoubleLettersAtWordEnd', this.illegalDoubleLettersAtWordEnd)
      }
      if (this.negatedWordStrengthMultiplier !== '') {
        options.push('negatedWordStrengthMultiplier', this.negatedWordStrengthMultiplier)
      }
      if (this.maxWordsBeforeSentimentToNegate !== '') {
        options.push('maxWordsBeforeSentimentToNegate', this.maxWordsBeforeSentimentToNegate)
      }
      if (this.maxWordsAfterSentimentToNegate !== '') {
        options.push('maxWordsAfterSentimentToNegate', this.maxWordsAfterSentimentToNegate)
      }
      if (this.MinSentencePosForQuotesIrony !== '') {
        options.push('MinSentencePosForQuotesIrony', this.MinSentencePosForQuotesIrony)
      }
      if (this.MinSentencePosForPunctuationIrony !== '') {
        options.push('MinSentencePosForPunctuationIrony', this.MinSentencePosForPunctuationIrony)
      }
      if (this.MinSentencePosForTermsIrony !== '') {
        options.push('MinSentencePosForTermsIrony', this.MinSentencePosForTermsIrony)
      }
      if (this.MinSentencePosForAllIrony !== '') {
        options.push('MinSentencePosForAllIrony', this.MinSentencePosForAllIrony)
      }

      return options
    },

    changeRadio (radio, cnt) {
      switch (cnt) {
        case 1:
          if (this.param1 === radio) {
            this.param1 = ''
          } else {
            this.param1 = radio
          }
          console.log(this.param1)
          break
        case 2:
          if (this.param2 === radio) {
            this.param2 = ''
          } else {
            this.param2 = radio
          }
          console.log(this.param2)
          break
        case 3:
          if (this.param3 === radio) {
            this.param3 = ''
          } else {
            this.param3 = radio
          }
          console.log(this.param3)
          break
      }
    },

    beforeUpload (file) {
      const isLt20M = file.size / 1024 / 1024 < 20
      if (!isLt20M) {
        this.$message.error('上传文件大小不能超过 20MB!')
      }
      return isLt20M
    }
  }
}
</script>

<style scoped>
.el-row {
  height: 400px;
  width: 100%;
  margin-bottom: 10px;
}
.el-col {
  border-radius: 4px;
  height: 60%;
  align-content: center;
  display: table;
}
el-upload {
  width: 80px;
  height: 80px;
  align-content: center;
}
.el-upload__tip {
  width: 80%;
  height: 80%;
  align-content: center;
  text-align: center;
  margin-left: 10%;
  margin-bottom: 10px;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #e5e9f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
  text-align: center;
  vertical-align: middle;
  display: table-cell;
  height: 80%;
  padding: 15px 50px;
  width: 50%;
}
.text-item {
  width: 50%;
  margin-bottom: 10px;
}
.file-upload {
  text-align: center;
}
.inputParam {
  margin-bottom: 10px;
  width: 50%;
  /*border: solid 1px  cornflowerblue;*/
  border-radius: 10px;
}
.checkboxParam {
  margin-bottom: 10px;
}

</style>
