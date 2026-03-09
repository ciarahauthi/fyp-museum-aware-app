import { beaconsService } from "../../services/beacons";
import { usersService } from "../../services/users";
import { categoriesService } from "../../services/categories";
import { exhibitsService } from "../../services/exhibits";
import { routesService } from "../../services/routes";

export const categories = ["Beacons", "Categories", "Exhibits", "Routes", "Users"];

// Table configurations
export const tableConfigs = {
  beacons: {
    tableName: "beacons",
    columns: [
      { key: "id", cellClassName: "bold" },
      { key: "name" },
      { key: "uuid", cellClassName: "mono" },
      { key: "major"},
      { key: "minor"},
      { key: "description", cellClassName: "mono"}
    ],
  },
  categories: {
    tableName: "categories",
    columns: [
      { key: "id", cellClassName: "bold" },
      { key: "name" },
      { key: "description" }
    ],
  },
  exhibits: {
    tableName: "exhibits",
    columns: [
      { key: "id", cellClassName: "bold" },
      { key: "title" },
      { key: "category" }
    ],
  },
  routes: {
    tableName: "routes",
    columns: [
      { key: "id", cellClassName: "bold" },
      { key: "name" }
    ],
  },
  users: {
    tableName: "users",
    columns: [
      { key: "id", cellClassName: "bold" },
      { key: "first_name" },
      { key: "surname" },
      { key: "email" }
    ],
  },
};

export const services = {
    beacons: beaconsService.getAll,
    categories: categoriesService.getAll,
    exhibits: exhibitsService.getAll,
    routes: routesService.getAll,
    users: usersService.getAll,
};