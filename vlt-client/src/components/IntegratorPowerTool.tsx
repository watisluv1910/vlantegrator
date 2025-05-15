import {useEffect, useRef, useState} from "react";
import {Box, Divider, IconButton, useTheme} from "@mui/material";
import {
    Save as SaveIcon,
    Handyman as BuildIcon,
    Start as StartIcon,
    StopCircle as StopIcon,
    RestartAlt as RestartIcon,
    DeleteForever as DeleteIcon,
    Bolt as BoltIcon,
} from "@mui/icons-material";

const CLOSED_SIZE = 48;
const ICONS_COUNT = 6;
const ICON_AREA = ICONS_COUNT * CLOSED_SIZE;

export const IntegratorPowerTool = () => {
    const [open, setOpen] = useState(false);

    const containerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (open && containerRef.current && !containerRef.current.contains(e.target as Node)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [open]);

    const theme = useTheme();

    const DIVIDER_GAP = parseFloat(theme.spacing(1)) * 2;
    const OPEN_MAX_HEIGHT = ICON_AREA + DIVIDER_GAP;

    return <Box
        ref={containerRef}
        onClick={() => setOpen(state => !state)}
        sx={{
            position: "fixed",
            bottom: 16,
            right: 16,
            zIndex: theme.zIndex.tooltip,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            backgroundColor: "background.paper",
            border: 1,
            borderColor: "divider",
            transition: theme.transitions.create(["height"], {
                duration: theme.transitions.duration.standard,
            }),
            borderRadius: open ? theme.shape.borderRadius * 2 : "50%",
            overflow: "hidden",
            width: CLOSED_SIZE,
            height: open ? OPEN_MAX_HEIGHT : CLOSED_SIZE,
        }}
    >
        {open ? (
            <>
                <IconButton aria-label="Сохранить маршрут">
                    <SaveIcon/>
                </IconButton>

                <Divider variant="middle" flexItem sx={{my: DIVIDER_GAP}}/>

                <IconButton aria-label="Собрать образ">
                    <BuildIcon/>
                </IconButton>
                <IconButton aria-label="Запустить контейнер">
                    <StartIcon/>
                </IconButton>
                <IconButton aria-label="Остановить контейнер">
                    <StopIcon/>
                </IconButton>
                <IconButton aria-label="Перезапустить контейнер">
                    <RestartIcon/>
                </IconButton>
                <IconButton aria-label="Удалить контейнер">
                    <DeleteIcon/>
                </IconButton>
            </>
        ) : (
            <IconButton aria-label="Центр управления" disableRipple>
                <BoltIcon name="powerToolIcon" sx={{color: theme.palette.accent.main}}/>
            </IconButton>
        )}
    </Box>;
}