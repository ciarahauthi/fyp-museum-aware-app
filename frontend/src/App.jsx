import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import Login from "./pages/login/Login";
import Home from "./pages/home/Home";
import ManageContent from "./pages/manage_content/ManageContent";
import ManageHomeScreen from "./pages/manage_homescreen/ManageHomeScreen";
import Settings from "./pages/settings/Settings";
import Edit from "./pages/edit/Edit";
import AppLayout from "./components/app_layout/AppLayout";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route element={<AppLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/manage-content" element={<ManageContent />} />
          <Route path="/manage-home-screen" element={<ManageHomeScreen />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/edit" element={<Edit />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
