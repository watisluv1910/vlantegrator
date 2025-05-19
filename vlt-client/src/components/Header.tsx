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
import {Alert} from "@/components/Alert.tsx";
import {THEME} from "@/styles/muiConfig.ts";
import {handleCopyEvent} from "@/utils/handlers.tsx";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {ACCOUNT_MANAGEMENT_URL} from "@/config/keycloak.ts";

export type HeaderProps = {
    currPath: string[];
};

const RENEW_AFTER_REDIRECT_FLAG: string = "renew_after_account_update";

export const Header = (props: HeaderProps) => {
    let auth: AuthContextProps = useAuth();

    const profile = auth.user!.profile;
    const email = profile?.email;
    const displayName = profile?.name;

    if (!displayName) {
        return (
            <Alert variant={"error"}>
                <h1>Имя пользователя не найдено, попробуйте обновить страницу
                    и совершить вход или проверьте настройки пользователя в Keycloak</h1>
            </Alert>
        );
    }

    if (!email) {
        return (
            <Alert variant={"error"}>
                <h1>Электронная почта пользователя не найдена, попробуйте обновить страницу
                    и совершить вход или проверьте настройки пользователя в Keycloak</h1>
            </Alert>
        );
    }

    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();
    const [sidebarOpen] = MainSidebarContext.useSidebarOpen();

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

    React.useEffect(() => {
        if (sessionStorage.getItem(RENEW_AFTER_REDIRECT_FLAG)) {
            sessionStorage.removeItem(RENEW_AFTER_REDIRECT_FLAG);
            auth
                .removeUser()
                .then(() => auth.signinRedirect())
                .catch((err) => console.error("Silent renew failed", err));
        }
    }, [auth]);

    const handleSettings = () => {
        sessionStorage.setItem(RENEW_AFTER_REDIRECT_FLAG, "1");
        window.location.href = ACCOUNT_MANAGEMENT_URL;
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
                                sx: {mt: 2},
                            },
                            list: {
                                sx: {py: 0}
                            }
                        }}
                    >
                        <MenuItem
                            divider
                            onClick={(e) => handleCopyEvent(e, email)}
                            sx={{cursor: "copy", "&:hover": {backgroundColor: "transparent"}}}
                        >
                            <Typography variant="subtitle2" color={THEME.palette.accent.main}>
                                {email}
                            </Typography>
                        </MenuItem>
                        <MenuItem divider onClick={handleSettings}>
                            <ListItemIcon>
                                <SettingsIcon fontSize="small"/>
                            </ListItemIcon>
                            <Typography variant="subtitle2">Настройки</Typography>
                        </MenuItem>
                        <MenuItem onClick={() => handleLogout(auth)}>
                            <ListItemIcon>
                                <LogoutIcon fontSize="small"/>
                            </ListItemIcon>
                            <Typography variant="subtitle2">Выход</Typography>
                        </MenuItem>
                    </Menu>
                </Box>
            </Toolbar>
        </AppBar>
    );
}