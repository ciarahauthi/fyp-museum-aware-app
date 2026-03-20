import { beaconsService } from "../../services/beacons";
import { usersService } from "../../services/users";
import { categoriesService } from "../../services/categories";
import { exhibitsService } from "../../services/exhibits";
import { routesService } from "../../services/routes";
import { imagesService } from "../../services/images";
import { homeService } from "../../services/home";
import { lazy } from "react";

export const categories = [
    "Beacons",
    "Categories",
    "Exhibits",
    "Routes",
    "Users",
];

// For add entry screen title
export const TABLE_SINGULAR = {
    beacons: "beacon",
    categories: "category",
    exhibits: "exhibit",
    routes: "route",
    users: "user",
    homescreen: "home screen item",
};

// Table configurations
export const tableConfigs = {
    beacons: {
        tableName: "beacons",
        columns: [
            { key: "id", cellClassName: "bold" },
            { key: "name" },
            { key: "uuid", cellClassName: "mono" },
            { key: "major" },
            { key: "minor" },
            { key: "description", cellClassName: "mono" },
        ],
    },
    categories: {
        tableName: "categories",
        columns: [
            { key: "id", cellClassName: "bold" },
            { key: "name" },
            { key: "description" },
        ],
    },
    exhibits: {
        tableName: "exhibits",
        columns: [
            { key: "id", cellClassName: "bold" },
            { key: "title" },
            { key: "category" },
        ],
    },
    routes: {
        tableName: "routes",
        columns: [
            { key: "id", cellClassName: "bold" }, 
            { key: "name" },
            { key: "exhibits" },
        ],
    },
    users: {
        tableName: "users",
        columns: [
            { key: "id", cellClassName: "bold" },
            { key: "first_name" },
            { key: "surname" },
            { key: "email" },
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

export const createServices = {
    beacons: (data) => beaconsService.create(data),
    categories: (data) => categoriesService.create(data),
    exhibits: (data) => exhibitsService.create(data),
    routes: (data) => routesService.create(data),
    users: (data) => usersService.create(data),
    homescreen: (data) => homeService.create(data),
};

export const updateServices = {
    beacons: (id, data) => beaconsService.update(id, data),
    categories: (id, data) => categoriesService.update(id, data),
    exhibits: (id, data) => exhibitsService.update(id, data),
    routes: (id, data) => routesService.update(id, data),
    users: (id, data) => usersService.update(id, data),
    homescreen: (id, data) => homeService.update(id, data),
};

export const deleteServices = {
    beacons: (id) => beaconsService.delete(id),
    categories: (id) => categoriesService.delete(id),
    exhibits: (id) => exhibitsService.delete(id),
    routes: (id) => routesService.delete(id),
    users: (id) => usersService.delete(id),
};

// Fields curators are allowed to edit
export const editFields = {
    beacons: [
        { key: "name", label: "Name", type: "text" },
        { key: "description", label: "Description", type: "textarea" },
        { key: "uuid", label: "UUID", type: "text" },
        { key: "major", label: "Major", type: "number" },
        { key: "minor", label: "Minor", type: "number" },
        {
            key: "location_id",
            label: "Location",
            type: "select",
            optionsKey: "locations",
            valueKey: "id",
            labelKey: "name",
        },
    ],
    categories: [
        { key: "name", label: "Name", type: "text" },
        { key: "description", label: "Description", type: "textarea" },
    ],
    exhibits: [
        { key: "title", label: "Title", type: "text" },
        { key: "description", label: "Description", type: "textarea" },
        { key: "child_friendly", label: "Child Friendly", type: "boolean" },
        { key: "is_loud", label: "Loud", type: "boolean" },
        { key: "is_crowded", label: "Crowded", type: "boolean" },
        { key: "is_dark", label: "Dark", type: "boolean" },
        {
            key: "beacon_id",
            label: "Beacon",
            type: "select",
            optionsKey: "beacons",
            valueKey: "id",
            labelKey: "name",
        },
        {
            key: "category_id",
            label: "Category",
            type: "select",
            optionsKey: "categories",
            valueKey: "id",
            labelKey: "name",
        },
        { key: "active", label: "Active", type: "boolean" },
        {
            key: "image_url",
            label: "Image",
            type: "image-select",
            optionsKey: "exhibit-images",
        },
    ],
    routes: [
        { key: "name", label: "Name", type: "text" },
        { key: "description", label: "Description", type: "textarea" },
        { key: "active", label: "Active", type: "boolean" },
        {
            key: "stops",
            label: "Exhibits",
            type: "referencearrayinput",
            optionsKey: "all-exhibits",
            valueKey: "id",
            labelKey: "title",
        },
        {
            key: "image_url",
            label: "Image",
            type: "image-select",
            optionsKey: "route-images",
        },
    ],
    users: [
        { key: "first_name", label: "First Name", type: "text" },
        { key: "surname", label: "Surname", type: "text" },
        { key: "email", label: "Email", type: "text" },
    ],
    homescreen: [
        { key: "title", label: "Title", type: "text" },
        { key: "description", label: "Description", type: "textarea" },
        { key: "section",
            label: "Section",
            type: "select",
            optionsKey: "home-sections",
            valueKey: "value",
            labelKey: "label",
        },
        { key: "active", label: "Active", type: "boolean" },
        { key: "image_url",
            label: "Image",
            type: "image-select",
            optionsKey: "home-images",
        },
    ],
};

// Extra fields shown only when adding (not editing)
export const addFields = {
    ...editFields,
    users: [
        ...editFields.users,
        { key: "password", label: "Password", type: "password" },
    ],
};

// Load functions for the fields
export const optionLoaders = {
    locations: () => routesService.getLocations(),
    categories: () => categoriesService.getAll(),
    beacons: () => beaconsService.getAll(),
    "exhibit-images": () => imagesService.getImages("exhibits"),
    "route-images": () => imagesService.getImages("routes"),
    "home-images": () => imagesService.getImages("home"),
    "all-exhibits": () => exhibitsService.getAll(),
    "home-sections": () => Promise.resolve([
        { value: "top", label: "Top" },
        { value: "mid", label: "Mid" },
        { value: "bottom", label: "Bottom" },
    ]),
};

// Read only fields
export const readOnlyFields = {
    beacons: [
        "id",
        "created_at",
        "updated_at",
        "creator_employee_id",
        "updated_employee_id",
    ],
    categories: [
        "id",
        "created_at",
        "updated_at",
        "creator_employee_id",
        "updated_employee_id",
    ],
    exhibits: [
        "id",
        "created_at",
        "updated_at",
        "creator_employee_id",
        "updated_employee_id",
    ],
    routes: ["id", "creator_employee_id", "updated_employee_id"],
    users: ["id"],
    homescreen: ["id", "created_at", "updated_at", "creator_employee_id", "updated_employee_id"],
};
