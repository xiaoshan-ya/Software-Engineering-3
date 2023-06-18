import request from '@/network/request'
let API = require('@/apis')

export const analyzeText = (data) => request._post(API.ANALYZE_BASIC, data)
export const analyzeFile = (data) => request._post(API.ANALYZE_FILE, data)
export const corpusOption = (config) => request._post(API.CORPUS_OPTION, null, config)

export const getSentiByTime = (config) => request._post(API.SENTI_TIME, null, config)

export const getSentiByVersion = (config) => request._post(API.SENTI_VERSION, null, config)
export const getSentiByUser = (config) => request._post(API.SENTI_USER, null, config)
