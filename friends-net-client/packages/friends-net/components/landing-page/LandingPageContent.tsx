import { Link } from "@mui/material";
import styles from '../../styles/Home.module.css'

const LandingPageContent: React.FC<{}> = () => {
    return (
        <div className={styles.grid}>
          <Link className={styles.card} href='/login'>
            <h2>Sign In &rarr;</h2>
            <p>Already have an account? Sign in and contact your friends!.</p>
          </Link>

          <Link className={styles.card} href='/register'>
            <h2>Sign Up &rarr;</h2>
            <p>Don&apos;t have an account yet? Don&apos;t worry you can register right now!</p>
          </Link>
        </div>
    )
}

export default LandingPageContent;