import request from '@/network/request'
import {
  COLLECTION_ADD,
  COLLECTION_DATA,
  COLLECTION_DATA_ADD,
  COLLECTION_DELETE,
  COLLECTION_LIST,
  DATA_LIST
} from '@/apis'

export const listAllData = (config) => request._post(DATA_LIST, null, config)
export const listAllCollection = (config) => request._post(COLLECTION_LIST, null, config)
export const listCollectionData = (config) => request._post(COLLECTION_DATA, null, config)
export const addCollection = (data) => request._post(COLLECTION_ADD, data)
export const addCollectionData = (data, config) => request._post(COLLECTION_DATA_ADD, data, config)
export const deleteCollection = (config) => request._post(COLLECTION_DELETE, null, config)
