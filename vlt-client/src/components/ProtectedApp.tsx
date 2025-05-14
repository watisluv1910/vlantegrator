import React, {useEffect, useState} from 'react';
import {AuthContextProps, hasAuthParams, useAuth} from 'react-oidc-context';
import {Alert} from './Alert';

type ProtectedAppProps = {
    children: React.ReactNode;
};

export const ProtectedApp : React.FC<ProtectedAppProps> = (props) => {
    const {children} = props;

    const auth: AuthContextProps = useAuth();
    const [hasTriedSignIn, setHasTriedSignIn] = useState(false);

    /**
     * Автоматический логин
     */
    useEffect(() => {
        if (!(hasAuthParams() || auth.isAuthenticated || auth.activeNavigator || auth.isLoading || hasTriedSignIn)) {
            void auth.signinRedirect();
            setHasTriedSignIn(true);
        }
    }, [auth, hasTriedSignIn]);

    if (auth.isLoading) {
        return (
            <>
                <h1>Loading...</h1>
            </>
        );
    }
    if (auth.error?.message) {
        return (
            <>
                <h1>Encountered a problem</h1>
                <Alert variant="error">{auth.error?.message}</Alert>
            </>
        );
    }
    if (!auth.isAuthenticated) {
        return (
            <>
                <h1>Encountered a problem</h1>
                <Alert variant="error">Unable to sign in</Alert>
            </>
        );
    }
    return <>{children}</>;
};