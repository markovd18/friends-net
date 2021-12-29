import '../styles/globals.css'
import type { AppProps } from 'next/app'
import { CookiesProvider } from 'react-cookie'

const NoProvider = ({children}) => <>{children}</>

function MyApp({ Component, pageProps }: AppProps) {
  const ContextProvider = Component.provider || NoProvider;

  return (
    <CookiesProvider>
      <ContextProvider>
        <Component {...pageProps} />
      </ContextProvider>
    </CookiesProvider>
  )
}

export default MyApp
