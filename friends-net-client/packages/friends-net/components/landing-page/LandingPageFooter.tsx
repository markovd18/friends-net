import { Link } from "@mui/material";
import styles from '../../styles/Home.module.css'

const LandingPageFooter: React.FC<{}> = () => {

    return (
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
    )
}

export default LandingPageFooter;