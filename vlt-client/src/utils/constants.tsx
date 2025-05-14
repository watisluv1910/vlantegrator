import {ReactElement} from "react";
import {
    Assessment as HealthIcon,
    Description as DocIcon,
    HelpCenter as HelpCenterIcon,
    Home as HomeIcon,
    Route as RouteIcon,
    Settings as SettingsIcon,
    Visibility as ObservIcon
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
    observer: {
        icon: <ObservIcon/>,
        text: "Обозреватель",
        protected: true,
        path: "/observer"
    },
    integrator: {
        icon: <RouteIcon/>,
        text: "Интегратор",
        protected: true,
        path: "/integrator"
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

export const BASE_API_URL = '/api/v1';