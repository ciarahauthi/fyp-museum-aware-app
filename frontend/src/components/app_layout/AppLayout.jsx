import { Outlet } from "react-router-dom";
import Sidebar from "../sidebar/Sidebar";
import "./AppLayout.css";

export default function AppLayout() {
  return (
    <div className="app-layout">
      <Sidebar />
      <main className="app-layout-content">
        <Outlet />
      </main>
    </div>
  );
}