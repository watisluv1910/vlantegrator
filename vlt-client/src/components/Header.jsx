import React, {useState} from "react";
import {useAuth} from "react-oidc-context";
import VLogo from "../../icons/svgs/vlt_logo_min.svg";
import {AppBar, Avatar, Box, Breadcrumbs, IconButton, Link, Toolbar, Typography} from "@mui/material";
import {useSidebarWidth, useSidebarOpen} from "../hooks/useSidebarWidth.jsx";

export const Header = ({path: currPath}) => {
    const auth = useAuth();
    const [sidebarWidth, _setSidebarWidth] = useSidebarWidth();
    const [sidebarOpen, _setSidebarOpen] = useSidebarOpen();
    const [path, setPath] = useState(currPath);

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
                        <Typography sx={{cursor: "pointer"}}>{auth.user.profile.name}</Typography>
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