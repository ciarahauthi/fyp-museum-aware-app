import { MdHome, MdEditNote, MdWeb, MdSettings, MdLogout } from "react-icons/md";

export const SidebarData = [
  {
    title: "Home",
    link: "/",
    icon: <MdHome />,
  },
  {
    title: "Manage Content",
    link: "/manage-content",
    icon: <MdEditNote />,
  },
  {
    title: "Manage Home Screen",
    link: "/manage-home-screen",
    icon: <MdWeb />,
  },
  {
    title: "Settings",
    link: "/settings",
    icon: <MdSettings />,
  },
  {
    title: "Logout",
    link: "/login",
    icon: <MdLogout />,
  },
];