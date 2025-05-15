import {createSidebarContext} from "./createSidebarContext.tsx";
import {DEFAULT_SIDEBAR_COLLAPSED_WIDTH, DEFAULT_SIDEBAR_OPENED_WIDTH} from "../utils/constants.tsx";

export const MAIN_SIDEBAR_OPENED_WIDTH = DEFAULT_SIDEBAR_OPENED_WIDTH;
export const MAIN_SIDEBAR_COLLAPSED_WIDTH = DEFAULT_SIDEBAR_COLLAPSED_WIDTH;

export const MainSidebarContext = createSidebarContext({
    openedWidth: MAIN_SIDEBAR_OPENED_WIDTH,
    collapsedWidth: MAIN_SIDEBAR_COLLAPSED_WIDTH
});

export const INTEGRATOR_SIDEBAR_OPENED_WIDTH = DEFAULT_SIDEBAR_OPENED_WIDTH;
export const INTEGRATOR_SIDEBAR_COLLAPSED_WIDTH = DEFAULT_SIDEBAR_COLLAPSED_WIDTH;

export const IntegratorSidebarContext = createSidebarContext({
    openedWidth: INTEGRATOR_SIDEBAR_OPENED_WIDTH,
    collapsedWidth: INTEGRATOR_SIDEBAR_COLLAPSED_WIDTH
});
