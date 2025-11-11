import React from 'react'
import { Routes, Route } from 'react-router-dom'
import { Layout } from 'antd'
import './App.css'

const { Header, Content, Footer } = Layout

const App: React.FC = () => {
  return (
    <Layout className="layout" style={{ minHeight: '100vh' }}>
      <Header>
        <div className="logo">AI驱动测试任务管理系统</div>
      </Header>
      <Content style={{ padding: '50px' }}>
        <Routes>
          <Route path="/" element={<div>欢迎使用AI驱动测试任务管理系统</div>} />
        </Routes>
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        AI Test Management System ©2025 Created by SynapseTest Team
      </Footer>
    </Layout>
  )
}

export default App
