import {createSidebarContext} from "./createSidebarContext.tsx";

export const MAIN_SIDEBAR_OPENED_WIDTH = 240;
export const MAIN_SIDEBAR_COLLAPSED_WIDTH = 60;

export const MainSidebarContext = createSidebarContext({
    openedWidth: MAIN_SIDEBAR_OPENED_WIDTH,
    collapsedWidth: MAIN_SIDEBAR_COLLAPSED_WIDTH
});

export const INTEGRATOR_SIDEBAR_OPENED_WIDTH = 300;
export const INTEGRATOR_SIDEBAR_COLLAPSED_WIDTH = 0;

export const IntegratorSidebarContext = createSidebarContext({
    openedWidth: INTEGRATOR_SIDEBAR_OPENED_WIDTH,
    collapsedWidth: INTEGRATOR_SIDEBAR_COLLAPSED_WIDTH
});
