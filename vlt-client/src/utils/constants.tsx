import {ReactElement} from "react";
import {
    Assessment as HealthIcon,
    Description as DocIcon,
    HelpCenter as HelpCenterIcon,
    Home as HomeIcon,
    Route as RouteIcon,
    Settings as SettingsIcon,
} from "@mui/icons-material";

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
    integrator: {
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

export const RECENT_ACTIVITY_POLLING_INTERVAL_MS = 13000;
export const BASIC_PLATFORM_HEALTH_POLLING_INTERVAL_MS = 8000;
