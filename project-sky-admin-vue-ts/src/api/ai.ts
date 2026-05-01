import request from '@/utils/request'

export const sendAIQuestion = (data: any) =>
  request({
    url: '/ai/chat',
    method: 'post',
    data
  })

export const getAIHistory = (params: any) =>
  request({
    url: '/ai/history',
    method: 'get',
    params
  })

export const getAISessions = (params: any) =>
  request({
    url: '/ai/sessions',
    method: 'get',
    params
  })

export const deleteAISession = (params: any) =>
  request({
    url: '/ai/session',
    method: 'delete',
    params
  })
