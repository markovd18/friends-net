import type { NextPage } from 'next'
import Head from 'next/head'
import styles from '../styles/Home.module.css'
import { useCookies } from 'react-cookie'
import UnauthNavbar from '../components/nav/UnauthNavbar'
import { Link } from '@mui/material'
import { useRouter } from 'next/router'

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
        <h1 className={styles.title}>
          Welcome to Friends Net!
        </h1>
       
        <p className={styles.description}>
          Get started by signing in.
        </p>

        <div className={styles.grid}>
          <Link className={styles.card} href='/login'>
            <h2>Sign In &rarr;</h2>
            <p>Already have an account? Sign in and contact your friends!.</p>
          </Link>

          <Link className={styles.card} href='/register'>
            <h2>Sign Up &rarr;</h2>
            <p>Don't have an account yet? Don't worry you can register right now!</p>
          </Link>
        </div>
      </main>

      <footer className={styles.footer}>
        <Link
          href="https://github.com/markovd18"
          target="_blank"
          rel="noopener noreferrer"
          underline='hover'
          color={'GrayText'}
        >
          Created by David Markov for KIV/PIA course.
          </Link>
      </footer>
    </div>
  )
}

export default Home
