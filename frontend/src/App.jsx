import { BrowserRouter, Routes, Route } from "react-router-dom";
import './App.css'
import TestDb from "./pages/testdb";
import Login from "./pages/Login";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/getUsers" element={<TestDb />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
