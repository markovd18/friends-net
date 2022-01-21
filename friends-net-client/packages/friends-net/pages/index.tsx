import type { NextPage } from 'next'
import Head from 'next/head'
import styles from '../styles/Home.module.css'
import { useCookies } from 'react-cookie'
import UnauthNavbar from '../components/nav/UnauthNavbar'
import { useRouter } from 'next/router'
import LandingPageFooter from '../components/landing-page/LandingPageFooter'
import LandingPageHeader from '../components/landing-page/LandingPageHeader'
import LandingPageContent from '../components/landing-page/LandingPageContent'

const Home: NextPage = () => {

  const [cookies] = useCookies(['accessToken']);
  const router = useRouter();
  if (cookies.accessToken) {
    router.push('/home');
  }


  return cookies.accessToken ? null : (
    <div className={styles.container}>

      <Head>
        <title>Friends Net</title>
        <meta name="description" content="Griends net homepage" />
      </Head>

      <UnauthNavbar />
      <main className={styles.main}>
        <LandingPageHeader />

        <LandingPageContent />
      </main>

      <LandingPageFooter />
    </div>
  )
}

export default Home
