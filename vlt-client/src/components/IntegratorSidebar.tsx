import React, {PropsWithChildren} from "react";
import {Box, IconButton, Paper, useTheme} from "@mui/material";
import {
    ChevronRight as ChevronRightIcon,
    ChevronLeft as ChevronLeftIcon,
} from "@mui/icons-material";
import {
    IntegratorSidebarContext,
    INTEGRATOR_SIDEBAR_COLLAPSED_WIDTH,
    INTEGRATOR_SIDEBAR_OPENED_WIDTH
} from "../hooks/sidebarContexts.tsx";

const BTN_SIZE = 40;
const OFFSET = 16;

export const IntegratorSidebar: React.FC<PropsWithChildren> = ({children: forms}) => {
    const [_, setWidth] = IntegratorSidebarContext.useSidebarWidth();
    const [isOpened, setOpen] = IntegratorSidebarContext.useSidebarOpen();

    const theme = useTheme();

    return (
        <>
            <Box
                sx={{
                    position: "fixed",
                    top: 60,
                    height: "100vh",
                    width: INTEGRATOR_SIDEBAR_OPENED_WIDTH,
                    right: isOpened ? 0 : -INTEGRATOR_SIDEBAR_OPENED_WIDTH,
                    boxSizing: "border-box",
                    overflow: "hidden",
                    transition: theme.transitions.create("right", {
                        duration: theme.transitions.duration.short
                    }),
                }}
            >
                <Paper
                    elevation={3}
                    sx={{
                        width: "100%",
                        height: "100%",
                        p: 2,
                        boxSizing: "border-box",
                    }}
                >
                    {forms}
                </Paper>
            </Box>

            <IconButton
                disableRipple
                onClick={() => {
                    setOpen(o => {
                        const next = !o;
                        setWidth(next
                            ? INTEGRATOR_SIDEBAR_OPENED_WIDTH
                            : INTEGRATOR_SIDEBAR_COLLAPSED_WIDTH
                        );
                        return next;
                    });
                }}
                sx={{
                    position: "fixed",
                    top: 62 + OFFSET,
                    zIndex: theme.zIndex.tooltip + 1,
                    width: BTN_SIZE,
                    height: BTN_SIZE,
                    bgcolor: "background.paper",
                    border: 1,
                    borderColor: "divider",
                    transition: theme.transitions.create("right", {
                        duration: theme.transitions.duration.short
                    }),
                    right: isOpened
                        ? INTEGRATOR_SIDEBAR_OPENED_WIDTH + OFFSET
                        : OFFSET,
                }}
            >
                {isOpened ? <ChevronRightIcon/> : <ChevronLeftIcon/>}
            </IconButton>
        </>
    );
};