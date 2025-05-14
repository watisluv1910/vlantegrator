import {useState} from "react";
import {AuthContextProps, useAuth} from "react-oidc-context";
import VLogo from "../../icons/svgs/vlt_logo_min.svg";
import {AppBar, Avatar, Box, Breadcrumbs, IconButton, Link, Toolbar, Typography} from "@mui/material";
import {useSidebarWidth, useSidebarOpen} from "../hooks/useSidebarState.tsx";
import {Alert} from "./Alert.tsx";

export type HeaderProps = {
    currPath: string[];
};

export const Header = (props: HeaderProps) => {
    const auth: AuthContextProps = useAuth();
    const userProfileName = auth.user?.profile?.name;

    if (!userProfileName) {
        return (
            <Alert variant={'error'}>
                <h1>Error, try reloading page and login again...</h1>
            </Alert>
        );
    }

    const [sidebarWidth] = useSidebarWidth();
    const [sidebarOpen] = useSidebarOpen();
    const [path, setPath] = useState(props.currPath);

    return (
        <>
            <AppBar
                position="fixed"
                sx={{
                    ml: sidebarWidth,
                    width: `calc(100% - ${sidebarWidth}px)`
                }}
            >
                <Toolbar>
                    <img
                        src={VLogo}
                        alt="Vlantegrator"
                        style={{width: 32, height: 32}}
                    />
                    {!sidebarOpen &&
                        <Typography fontFamily="Madimi One, sans-serif" variant="h3" noWrap component="div">
                            lantegrator
                        </Typography>}
                    <Box sx={{flexGrow: 1, display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                        <Breadcrumbs separator="›" color='white' aria-label="breadcrumb">
                            {path.map((crumb, idx) => (
                                <Link
                                    key={idx}
                                    underline="hover"
                                    color={idx === path.length - 1 ? 'accent' : 'white'}
                                    href="#" // TODO:
                                    onClick={() => setPath(path.slice(0, idx + 1))}
                                    sx={{cursor: 'pointer'}}
                                >
                                    {crumb}
                                </Link>
                            ))}
                        </Breadcrumbs>
                    </Box>
                    <Box
                        sx={{
                            display: 'flex',
                            flexDirection: 'row',
                            justifyContent: 'center',
                            alignItems: 'center',
                            gap: 1,
                        }}
                    >
                        <Typography sx={{cursor: "pointer"}}>{userProfileName}</Typography>
                        <IconButton sx={{ml: 'auto', width: 40, height: 40}}>
                            <Avatar/>
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>

            <Toolbar/>
        </>
    );
}