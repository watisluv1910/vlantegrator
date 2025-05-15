import React, {useState} from "react";
import {AuthContextProps, useAuth} from "react-oidc-context";
import VLogo from "../../icons/svgs/vlt_logo_min.svg";
import {
    AppBar,
    Avatar,
    Box,
    Breadcrumbs,
    IconButton,
    Link,
    ListItemIcon,
    Menu,
    MenuItem,
    Toolbar,
    Typography
} from "@mui/material";
import {
    Logout as LogoutIcon,
    Settings as SettingsIcon,
} from '@mui/icons-material';
import {useSidebarWidth, useSidebarOpen} from "../hooks/useSidebarState.tsx";
import {Alert} from "./Alert.tsx";
import {THEME} from "../styles/muiConfig.ts";
import {handleCopyEvent} from "../utils/handlers.ts";

export type HeaderProps = {
    currPath: string[];
};

export const Header = (props: HeaderProps) => {
    const auth: AuthContextProps = useAuth();

    const profile = auth.user!.profile;
    const email = profile?.email;
    const displayName = profile?.name;

    if (!displayName) {
        return (
            <Alert variant={"error"}>
                <h1>Error displaying user name, try reloading page and login again or refine user configuration in
                    Keycloak</h1>
            </Alert>
        );
    }

    if (!email) {
        return (
            <Alert variant={"error"}>
                <h1>User email not found, try reloading page and login again or refine user configuration in
                    Keycloak</h1>
            </Alert>
        );
    }

    const [sidebarWidth] = useSidebarWidth();
    const [sidebarOpen] = useSidebarOpen();

    const [path, setPath] = useState(props.currPath);

    const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
    const open = Boolean(menuAnchor);

    const handleMenuOpen = (e: React.MouseEvent<HTMLElement>) => {
        setMenuAnchor(e.currentTarget);
    };

    const handleMenuClose = () => {
        setMenuAnchor(null);
    };

    const handleLogout = (authProps: AuthContextProps) => {
        handleMenuClose();
        authProps.signoutRedirect().then(_ => console.log("Logout successful"));
    };

    const handleSettings = () => {
        handleMenuClose();
    };

    return (
        <AppBar
            position="sticky"
            sx={{
                top: 0,
                ml: `${sidebarWidth}px`,
                width: `calc(100% - ${sidebarWidth}px)`,
                zIndex: THEME.zIndex.drawer + 1,
            }}
        >
            <Toolbar>
                <img src={VLogo} alt="Vlantegrator" style={{width: 32, height: 32}}/>
                {!sidebarOpen && (
                    <Typography fontFamily="Madimi One, sans-serif" variant="h3" noWrap sx={{cursor: "default"}}>
                        lantegrator
                    </Typography>
                )}
                <Box sx={{flexGrow: 1, display: "flex", justifyContent: "center", alignItems: "center"}}>
                    <Breadcrumbs separator="›" color={THEME.palette.white.main} aria-label="breadcrumb">
                        {path.map((crumb, idx) => (
                            <Link
                                key={idx}
                                underline="hover"
                                color={idx === path.length - 1 ? THEME.palette.accent.main : THEME.palette.white.main}
                                onClick={() => setPath(path.slice(0, idx + 1))}
                                sx={{cursor: "pointer"}}
                            >
                                {crumb}
                            </Link>
                        ))}
                    </Breadcrumbs>
                </Box>
                <Box sx={{display: "flex", alignItems: "center", gap: 1}}>
                    <Typography sx={{cursor: "default"}}>{displayName}</Typography>
                    <IconButton onClick={handleMenuOpen} size="small" sx={{ml: 1, cursor: "pointer"}}>
                        <Avatar/>
                    </IconButton>
                    <Menu
                        anchorEl={menuAnchor}
                        open={open}
                        onClose={handleMenuClose}
                        transformOrigin={{horizontal: "right", vertical: "top"}}
                        anchorOrigin={{horizontal: "right", vertical: "bottom"}}
                        slotProps={{
                            paper: {
                                elevation: 2,
                                sx: {mt: 1.5, minWidth: 200},
                            }
                        }}
                    >
                        <MenuItem
                            onClick={(e) => handleCopyEvent(e, email)}
                            sx={{cursor: "copy", "&:hover": {backgroundColor: "transparent"}}}
                        >
                            <Typography variant="subtitle2" color={THEME.palette.accent.main}>
                                {email}
                            </Typography>
                        </MenuItem>
                        <MenuItem onClick={handleSettings}>
                            <ListItemIcon>
                                <SettingsIcon fontSize="small"/>
                            </ListItemIcon>
                            <Typography variant="subtitle2">Settings</Typography>
                        </MenuItem>
                        <MenuItem onClick={() => handleLogout(auth)}>
                            <ListItemIcon>
                                <LogoutIcon fontSize="small"/>
                            </ListItemIcon>
                            <Typography variant="subtitle2">Log Out</Typography>
                        </MenuItem>
                    </Menu>
                </Box>
            </Toolbar>
        </AppBar>
    );
}