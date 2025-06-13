import {ReactElement} from "react";
import {
    Assessment as HealthIcon,
    Description as DocIcon,
    HelpCenter as HelpCenterIcon,
    Home as HomeIcon,
    Route as RouteIcon,
    Settings as SettingsIcon,
} from "@mui/icons-material";
import {Position} from "@xyflow/react";

export type Route = {
    icon: ReactElement;
    text: string;
    protected: boolean;
    path: string;
};

export const MAIN_ROUTES: Record<string, Route> = {
    home: {
        icon: <HomeIcon/>,
        text: "Главная",
        protected: true,
        path: "/"
    },
    observer: {
        icon: <RouteIcon/>,
        text: "Маршруты",
        protected: true,
        path: "/routes"
    },
    health: {
        icon: <HealthIcon/>,
        text: "Состояние",
        protected: true,
        path: "/health"
    },
    docs: {
        icon: <DocIcon/>,
        text: "Документация",
        protected: true,
        path: "/docs"
    },
    help: {
        icon: <HelpCenterIcon/>,
        text: "Помощь",
        protected: true,
        path: "/help"
    },
    settings: {
        icon: <SettingsIcon/>,
        text: "Настройки",
        protected: true,
        path: "/settings"
    },
};

export const AXIOS_RETRIES_COUNT = 1;
export const AXIOS_RETRIES_DELAY_MS = 500;

const host = import.meta.env.VITE_BACKEND_HOST;
const port = import.meta.env.VITE_BACKEND_PORT;

if (!host || !port) {
    console.warn(
        "[constants.ts] VITE_BACKEND_HOST or VITE_BACKEND_PORT is not defined"
    );
}

export const BASE_API_URL = `http://${host ?? "localhost"}:${port ?? "8080"}`;

export const DOCS: { label: string; url: string }[] = [
    {
        label: "🌐 GitHub – Репозиторий Vlantegrator",
        url: "https://github.com/watisluv1910/vlantegrator",
    },
    {
        label: "📘 Integrator API – Swagger UI",
        url: `${BASE_API_URL}/swagger-ui/index.html`,
    },
    {
        label: "📄 Dokka – Модуль Integrator",
        url: `http://${host ?? "localhost"}:8079/docs/integrator/index.html`,
    },
    {
        label: "📄 Dokka – Модуль Builder",
        url: `http://${host ?? "localhost"}:8079/docs/builder/index.html`,
    },
    {
        label: "📄 Dokka – Модуль Deployer",
        url: `http://${host ?? "localhost"}:8079/docs/deployer/index.html`,
    },
];

export const RECENT_ACTIVITY_POLLING_INTERVAL_MS = 13000;
export const BASIC_PLATFORM_HEALTH_POLLING_INTERVAL_MS = 8000;

export const ROUTES_SEARCH_DEBOUNCE_MS = 800;

// Настройки редактора маршрутов

export const SNAP_SIZE: [number, number] = [20, 20];
export const ADAPTER_PREVIEW_SIZE = 48;

export const FLOW_NODE_DEFAULT_SIZE = 60;
export const FLOW_NODE_DEFAULTS = {
    sourcePosition: Position.Bottom,
    targetPosition: Position.Top,
};
