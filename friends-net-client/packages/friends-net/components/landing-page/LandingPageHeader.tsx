
import styles from '../../styles/Home.module.css'

const LandingPageHeader: React.FC<{}> = () => {
    return (
        <>
            <h1 className={styles.title}>
            Welcome to Friends Net!
            </h1>
        
            <p className={styles.description}>
            Get started by signing in.
            </p>
        </>
    )
}

export default LandingPageHeader;