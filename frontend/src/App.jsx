import { BrowserRouter, Routes, Route } from "react-router-dom";
import './App.css'
import TestDb from "./pages/testdb";

function App() {
  return (
    <BrowserRouter>
      <Routes>
      <Route path="/getUsers" element={<TestDb />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
