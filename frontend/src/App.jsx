import { useState } from 'react'
import Auth from './components/Auth'
import { Routes, Route } from "react-router-dom";
import Dashboard from './components/Dashboard';

function App() {
  const [count, setCount] = useState(0)

  return (
    <Routes>
      <Route path="/" element={<Auth />} />
      <Route path="/login" element={<Auth />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  )
}

export default App
